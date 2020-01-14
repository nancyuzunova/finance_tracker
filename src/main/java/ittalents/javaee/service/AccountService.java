package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.mail.MailSender;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class AccountService {

    private static final int MAX_AMOUNT_OF_PLANNED_PAYMENT = 2500;

    private AccountRepository accountRepository;
    private TransferService transferService;
    private TransactionService transactionService;
    private PlannedPaymentRepository paymentRepository;
    private CategoryService categoryService;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferService transferService,
                          TransactionService transactionService, PlannedPaymentRepository paymentRepository,
                          CategoryService categoryService) {
        this.accountRepository = accountRepository;
        this.transferService = transferService;
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.categoryService = categoryService;
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

    public AccountDto createAccount(User user, AccountDto accountDto) {
        Account a = new Account();
        a.fromDto(accountDto);
        a.setCreatedOn(LocalDateTime.now());
        a.setUser(user);
        return this.accountRepository.save(a).toDto();
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

    public ResponseTransferDto makeTransfer(RequestTransferDto requestTransferDto) {
        if (requestTransferDto.getFromAccountId() == requestTransferDto.getToAccountId()) {
            throw new InvalidOperationException("You cannot make transfer to the same account!");
        }
        if (requestTransferDto.getDate().after(new Date())) {
            throw new InvalidOperationException("You cannot make future transfers!");
        }
        if(LocalDate.of(1900, 1, 1).isAfter(requestTransferDto.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate())){
            throw new InvalidOperationException("Invalid date! Please try again!");
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
            return transferService.getAllTransfersForUser(userId);
        }
        return transferService.getTransfersByAccountId(accountId);
    }

    public ResponseTransactionDto makeTransaction(RequestTransactionDto requestTransactionDto) {
        if(LocalDate.of(1900, 1, 1).isAfter(requestTransactionDto.getDate().toInstant()
        .atZone(ZoneId.systemDefault()).toLocalDate())){
            throw new InvalidOperationException("Invalid date! Please try again!");
        }
        if (requestTransactionDto.getDate().after(new Date())) {
            throw new InvalidOperationException("You cannot make future transactions!");
        }
        return this.transactionService.createTransaction(requestTransactionDto);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void payPlannedPayments() {
        Date today = new Date();
        List<PlannedPayment> payments = paymentRepository.findAllByDateAndStatus(today, PlannedPayment.PaymentStatus.ACTIVE);
        for (PlannedPayment payment : payments) {
            double amount = payment.getAmount();
            double availability = payment.getAccount().getBalance();
            if (availability < amount) {
                String userEmail = payment.getAccount().getUser().getEmail();
                Thread sender = new Thread() {
                    @Override
                    public void run() {
                        MailSender.sendMail(userEmail, "NOT finished payment", "Hello,\nYour planned payment " +
                                payment.getTitle() + " has failed because of  insufficient balance of your account. " +
                                "Please deposit to your account and make payment manually!");
                    }
                };
                sender.start();
            }
            else {
                pay(payment);
            }
        }
    }

    @Transactional
    protected void pay(PlannedPayment payment) {
        RequestTransactionDto transaction = new RequestTransactionDto();
        transaction.setAccountId(payment.getAccount().getId());
        transaction.setAmount(payment.getAmount());
        transaction.setCurrency(payment.getAccount().getCurrency());
        transaction.setCategoryId(payment.getCategory().getId());
        transaction.setDate(payment.getDate());
        transaction.setType(Type.EXPENSE);
        transaction.setDescription(payment.getTitle());
        makeTransaction(transaction);
        payment.setStatus(PlannedPayment.PaymentStatus.PAID);
        this.paymentRepository.save(payment);
    }

    public ResponsePlannedPaymentDto createPlannedPayment(RequestPlannedPaymentDto requestPlannedPaymentDto) {
        if (requestPlannedPaymentDto.getDate().before(new Date())) {
            throw new InvalidOperationException("You cannot make planned payments with past dates!");
        }
        if (requestPlannedPaymentDto.getAmount() > MAX_AMOUNT_OF_PLANNED_PAYMENT) {
            throw new InvalidOperationException("You can not make planned payment exceeding the maximum amount!");
        }
        PlannedPayment plannedPayment = new PlannedPayment();
        Account account = getAccountById(requestPlannedPaymentDto.getAccountId());
        Category category = categoryService.getCategoryById(requestPlannedPaymentDto.getCategoryId());
        plannedPayment.setAccount(account);
        plannedPayment.setCategory(category);
        plannedPayment.fromDto(requestPlannedPaymentDto);
        return this.paymentRepository.save(plannedPayment).toDto();
    }
}
