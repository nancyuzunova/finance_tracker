package ittalents.javaee.service;

import ittalents.javaee.controller.SessionManager;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransferDao;
import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.mail.MailSender;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final int MAX_AMOUNT_OF_PLANNED_PAYMENT = 2500;

    private AccountRepository accountRepository;
    private TransferService transferService;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private PlannedPaymentRepository paymentRepository;
    private TransferDao transferDao;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferService transferService,
                          TransactionService transactionService, BudgetService budgetService,
                          PlannedPaymentRepository paymentRepository,
                          TransferDao transferDao) {
        this.accountRepository = accountRepository;
        this.transferService = transferService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.paymentRepository = paymentRepository;
        this.transferDao = transferDao;
    }

    public List<AccountDto> getAllAccountsByUserId(long userId) {
        return accountRepository.findAllByUserId(userId).stream().map(Account::toDto).collect(Collectors.toList());
    }

    public Account getAccountById(long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent()) {
            return account.get();
        }
        throw new ElementNotFoundException("Account with id = " + accountId + " does not exist!");
    }

    public long createAccount(User user, AccountDto accountDto) {
        Account a = new Account();
        a.fromDto(accountDto);
        a.setCreatedOn(LocalDateTime.now());
        a.setUser(user);
        return this.accountRepository.save(a).getId();
    }

    public Account changeAccountCurrency(long accountId, Currency currency) {
        Account account = getAccountById(accountId);
        double balance = account.getBalance();
        double convertedBalance = CurrencyConverter.convert(account.getCurrency(), currency, balance);
        account.setBalance(convertedBalance);
        account.setCurrency(currency);
        return this.accountRepository.save(account);
    }

    public void deleteAccount(long accountId) {
        this.accountRepository.deleteById(accountId);
    }

    public long makeTransfer(RequestTransferDto requestTransferDto) {
        if (requestTransferDto.getFromAccountId() == requestTransferDto.getToAccountId()) {
            throw new InvalidOperationException("You cannot make transfer to the same account!");
        }
        if (requestTransferDto.getDate().after(new Date())) {
            throw new InvalidOperationException("You cannot make future transfers!");
        }
        Account accountFrom = getAccountById(requestTransferDto.getFromAccountId());
        Account accountTo = getAccountById(requestTransferDto.getToAccountId());
        if (accountFrom.getUser().getId() != accountTo.getUser().getId()) {
            throw new InvalidOperationException("You can not make transfer to other users!");
        }
        double amount = requestTransferDto.getAmount();
        double fromAmount = CurrencyConverter.convert(requestTransferDto.getCurrency(), accountFrom.getCurrency(), amount);
        double toAmount = CurrencyConverter.convert(requestTransferDto.getCurrency(), accountTo.getCurrency(), amount);
        if (accountFrom.getBalance() >= fromAmount) {
            // make transfer
            accountFrom.setBalance(accountFrom.getBalance() - fromAmount);
            accountTo.setBalance(accountTo.getBalance() + toAmount);
            this.accountRepository.save(accountFrom);
            this.accountRepository.save(accountTo);
            return this.transferService.createTransfer(accountFrom, accountTo, requestTransferDto);
        } else {
            throw new InvalidOperationException("Not enough balance!");
        }
    }

    public List<ResponseTransferDto> getTransfersByAccountId(long userId, long accountId) throws SQLException {
        if (accountId == 0) {
            return transferDao.getLoggedUserTransfers(userId);
        }
        return transferService.getTransfersByAccountId(accountId);
    }

    public long makeTransaction(RequestTransactionDto requestTransactionDto) {
        if (requestTransactionDto.getDate().after(new Date())) {
            throw new InvalidOperationException("You cannot make future transactions!");
        }
        Account account = getAccountById(requestTransactionDto.getAccountId());
        double amount = requestTransactionDto.getAmount();
        if (!requestTransactionDto.getCurrency().equals(account.getCurrency())) {
            amount = CurrencyConverter.convert(requestTransactionDto.getCurrency(), account.getCurrency(), amount);
        }
        if (Type.EXPENSE.equals(requestTransactionDto.getType()) && account.getBalance() < amount) {
            throw new InvalidOperationException("Not enough account balance!");
        }
        if (Type.EXPENSE.equals(requestTransactionDto.getType())) {
            account.setBalance(account.getBalance() - amount);
        } else {
            account.setBalance(account.getBalance() + amount);
        }
        accountRepository.save(account);
        return this.transactionService.createTransaction(account.getId(), requestTransactionDto);
    }

    public long addBudget(RequestBudgetDto requestBudgetDto) {
        if (requestBudgetDto.getFromDate().after(requestBudgetDto.getToDate())) {
            throw new InvalidOperationException("Incorrect dates! Please try again!");
        }
        Account account = getAccountById(requestBudgetDto.getAccountId());
        if (account.getBalance() < requestBudgetDto.getAmount()) {
            throw new InvalidOperationException("The budget can not exceed the account balance!");
        }
        return budgetService.createBudget(requestBudgetDto);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void payPlannedPayments(HttpSession session) {
        Date today = new Date();
        List<PlannedPayment> payments = paymentRepository.findAllByDateAndStatus(today, PlannedPayment.PaymentStatus.ACTIVE);
        payments.sort((p1, p2) -> Double.compare(p1.getAmount(), p2.getAmount()));
        for (PlannedPayment payment : payments) {
            double amount = payment.getAmount();
            double availability = payment.getAccount().getBalance();
            if (availability < amount) {
                UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
                Thread sender = new Thread() {
                    @Override
                    public void run() {
                        MailSender.sendMail(user.getEmail(), "NOT finished payment", "Hello,\nYour planned payment " +
                                payment.getTitle() + " has failed because of  insufficient balance of your account. " +
                                "Please deposit to your account and make payment manually!");
                    }
                };
                sender.start();
            }
            payment.getAccount().setBalance(availability - amount);
            payment.setStatus(PlannedPayment.PaymentStatus.PAID);
            this.paymentRepository.save(payment);
        }
    }

    public long createPlannedPayment(RequestPlannedPaymentDto requestPlannedPaymentDto) {
        if(requestPlannedPaymentDto.getDate().before(new Date())){
            throw new InvalidOperationException("You cannot make planned payments with past dates!");
        }
        PlannedPayment plannedPayment = new PlannedPayment();
        if (requestPlannedPaymentDto.getAmount() > MAX_AMOUNT_OF_PLANNED_PAYMENT) {
            throw new InvalidOperationException("You can not make planned payment exceeding the maximum amount!");
        }
        Account account = getAccountById(requestPlannedPaymentDto.getAccountId());
        plannedPayment.setAccount(account);
        plannedPayment.setTitle(requestPlannedPaymentDto.getTitle());
        plannedPayment.setStatus(PlannedPayment.PaymentStatus.ACTIVE);
        plannedPayment.setAmount(requestPlannedPaymentDto.getAmount());
        plannedPayment.setDate(requestPlannedPaymentDto.getDate());
        return this.paymentRepository.save(plannedPayment).getId();
    }
}
