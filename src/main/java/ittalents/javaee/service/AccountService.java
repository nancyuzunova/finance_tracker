package ittalents.javaee.service;

import ittalents.javaee.Util;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.mail.MailSender;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private TransactionDao transactionDao;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferService transferService,
                          TransactionService transactionService, PlannedPaymentRepository paymentRepository,
                          CategoryService categoryService, TransactionDao transactionDao) {
        this.accountRepository = accountRepository;
        this.transferService = transferService;
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.categoryService = categoryService;
        this.transactionDao = transactionDao;
    }

    public List<AccountDto> getAllAccountsByUserId(long userId) {
        return accountRepository.findAllByUserId(userId).stream().map(Account::toDto).collect(Collectors.toList());
    }

    public Account getAccountById(long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isPresent()) {
            return account.get();
        }
        throw new ElementNotFoundException(Util.getNotExistingErrorMessage("Account", accountId));
    }

    public AccountDto createAccount(User user, AccountDto accountDto) {
        Account account = new Account();
        account.fromDto(accountDto);
        account.setCreatedOn(LocalDateTime.now());
        account.setUser(user);
        return this.accountRepository.save(account).toDto();
    }

    public Account changeAccountCurrency(long accountId, Currency currency) {
        Account account = getAccountById(accountId);
        double balance = account.getBalance();
        double convertedBalance = CurrencyConverter.convert(account.getCurrency(), currency, balance);
        account.setBalance(convertedBalance);
        account.setCurrency(currency);
        return this.accountRepository.save(account);
    }

    public ResponseTransferDto makeTransfer(RequestTransferDto requestTransferDto) {
        if (requestTransferDto.getFromAccountId() == requestTransferDto.getToAccountId()) {
            throw new InvalidOperationException(Util.DUPLICATED_ACCOUNT);
        }
        if (requestTransferDto.getDate().after(new Date())) {
            throw new InvalidOperationException(Util.replacePlaceholder("transfers", Util.FUTURE_OPERATION));
        }
        if (Util.MIN_DATE.isAfter(Util.getConvertedDate(requestTransferDto.getDate()))) {
            throw new InvalidOperationException(Util.INVALID_DATE);
        }
        Account accountFrom = getAccountById(requestTransferDto.getFromAccountId());
        Account accountTo = getAccountById(requestTransferDto.getToAccountId());
        if (accountFrom.getUser().getId() != accountTo.getUser().getId()) {
            throw new InvalidOperationException(Util.TRANSFER_TO_OTHER_USER);
        }
        double amount = requestTransferDto.getAmount();
        double amountInSenderCurrency = CurrencyConverter.convert(requestTransferDto.getCurrency(), accountFrom.getCurrency(), amount);
        double amountInReceiverCurrency = CurrencyConverter.convert(requestTransferDto.getCurrency(), accountTo.getCurrency(), amount);
        if (accountFrom.getBalance() >= amountInSenderCurrency) {
            // make transfer
            accountFrom.setBalance(accountFrom.getBalance() - amountInSenderCurrency);
            accountTo.setBalance(accountTo.getBalance() + amountInReceiverCurrency);
            this.accountRepository.save(accountFrom);
            this.accountRepository.save(accountTo);
            return this.transferService.createTransfer(accountFrom, accountTo, requestTransferDto);
        } else {
            throw new InvalidOperationException(Util.NOT_ENOUGH_BALANCE);
        }
    }

    public List<ResponseTransferDto> getTransfersByAccountId(long userId, long accountId) throws SQLException {
        if (accountId == 0) {
            return transferService.getAllTransfersForUser(userId);
        }
        return transferService.getTransfersByAccountId(accountId);
    }

    public ResponseTransactionDto makeTransaction(RequestTransactionDto requestTransactionDto) {
        if (Util.MIN_DATE.isAfter(Util.getConvertedDate(requestTransactionDto.getDate()))) {
            throw new InvalidOperationException(Util.INVALID_DATE);
        }
        if (requestTransactionDto.getDate().after(new Date())) {
            throw new InvalidOperationException(Util.replacePlaceholder("transactions", Util.FUTURE_OPERATION));
        }
        return this.transactionService.createTransaction(requestTransactionDto);
    }

    public ResponsePlannedPaymentDto createPlannedPayment(RequestPlannedPaymentDto requestPlannedPaymentDto) {
        if (requestPlannedPaymentDto.getDate().before(new Date())) {
            throw new InvalidOperationException(Util.PAST_PLANNED_PAYMENTS);
        }
        if (Util.MAX_DATE.isBefore(Util.getConvertedDate(requestPlannedPaymentDto.getDate()))) {
            throw new InvalidOperationException(Util.FAR_PLANNED_PAYMENTS);
        }
        if (requestPlannedPaymentDto.getAmount() > MAX_AMOUNT_OF_PLANNED_PAYMENT) {
            throw new InvalidOperationException(Util.EXCEEDING_PLANNED_PAYMENTS);
        }
        PlannedPayment plannedPayment = new PlannedPayment();
        Account account = getAccountById(requestPlannedPaymentDto.getAccountId());
        Category category = categoryService.getCategoryById(requestPlannedPaymentDto.getCategoryId());
        plannedPayment.setAccount(account);
        plannedPayment.setCategory(category);
        plannedPayment.fromDto(requestPlannedPaymentDto);
        return this.paymentRepository.save(plannedPayment).toDto();
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
                        MailSender.sendMail(userEmail, Util.EMAIL_SUBJECT,
                                Util.replacePlaceholder(payment.getTitle(), Util.EMAIL_BODY));
                    }
                };
                sender.start();
            } else {
                pay(payment);
            }
        }
    }

    @Transactional
    protected void pay(PlannedPayment payment) {
        RequestTransactionDto transaction = new RequestTransactionDto();
        transaction.setAccountId(payment.getAccount().getId());
        transaction.setAmount(payment.getAmount());
        transaction.setCurrency(payment.getCurrency());
        transaction.setCategoryId(payment.getCategory().getId());
        transaction.setDate(payment.getDate());
        transaction.setType(Type.EXPENSE);
        transaction.setDescription(payment.getTitle());
        makeTransaction(transaction);
        payment.setStatus(PlannedPayment.PaymentStatus.PAID);
        this.paymentRepository.save(payment);
    }

    @Transactional
    public void deleteAccount(long accountId) throws SQLException {
        this.transactionService.deleteTransactionByAccountId(accountId);
        this.transferService.deleteTransferByAccountId(accountId);
        this.paymentRepository.deleteByAccount_Id(accountId);
        transactionDao.deleteAccountById(accountId);
    }
}
