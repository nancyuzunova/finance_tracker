package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.BudgetDto;
import ittalents.javaee.model.dto.TransactionDto;
import ittalents.javaee.model.dto.TransferDto;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private TransferService transferService;
    private TransactionService transactionService;
    private BudgetService budgetService;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferService transferService,
                          TransactionService transactionService, BudgetService budgetService) {
        this.accountRepository = accountRepository;
        this.transferService = transferService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    public List<AccountDto> getAllAccounts() {
        List<AccountDto> accounts = new ArrayList<>();
        for (Account account : accountRepository.findAll()) {
            accounts.add(account.toDto());
        }
        return accounts;
    }

    public List<AccountDto> getAllAccountsByUserId(long id) {
        return accountRepository.findAllByUserId(id).stream().map(Account::toDto).collect(Collectors.toList());
    }

    public Account getAccountById(long id) {
        Optional<Account> account = accountRepository.findById(id);

        if (account.isPresent()) {
            return account.get();
        }

        throw new ElementNotFoundException("Account with id = " + id + " does not exist!");
    }

    public long createAccount(User user, AccountDto accountDto) {
        Account a = new Account();
        a.fromDto(accountDto);
        a.setCreatedOn(LocalDateTime.now());
        a.setUser(user);
        return this.accountRepository.save(a).getId();
    }

    public Account changeAccountCurrency(long id, Currency currency) {
        Optional<Account> accountById = accountRepository.findById(id);

        if (!accountById.isPresent()) {
            throw new ElementNotFoundException("Account with id = " + id + " does not exist!");
        }

        Account account = accountById.get();

        double balance = account.getBalance();
        double convertedBalance = CurrencyConverter.convert(account.getCurrency(), currency, balance);
        account.setBalance(convertedBalance);
        account.setCurrency(currency);
        return this.accountRepository.save(account);
    }

    public void deleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }

    public long makeTransfer(TransferDto transferDto) {
        Account accountFrom;
        Account accountTo;

        try {
            accountFrom = getAccountById(transferDto.getFromAccountId());
        } catch (NoSuchElementException e) {
            throw new InvalidOperationException(
                    "Account with id " + transferDto.getFromAccountId() + " does not exists!");
        }

        try {
            accountTo = getAccountById(transferDto.getToAccountId());
        } catch (NoSuchElementException e) {
            throw new InvalidOperationException(
                    "Account with id " + transferDto.getToAccountId() + " does not exists!");
        }

        if (accountFrom.getUser().getId() != accountTo.getUser().getId()) {
            throw new InvalidOperationException("You can not make transfer to other users!");
        }

        double amount = transferDto.getAmount();
        if (accountFrom.getBalance() >= amount) {
            // make transfer
            accountFrom.setBalance(accountFrom.getBalance() - amount);
            accountTo.setBalance(accountTo.getBalance() + amount);

            this.accountRepository.save(accountFrom);
            this.accountRepository.save(accountTo);
            return this.transferService.createTransfer(transferDto);
        } else {
            throw new InvalidOperationException("Not enough balance!");
        }
    }

    public List<TransferDto> getTransfersByAccountId(long id) {
        if (!accountRepository.existsById(id)) {
            throw new ElementNotFoundException("Account with id = " + id + " does not exist!");
        }
        return transferService.getTransfersByAccountId(id);
    }

    public long makeTransaction(long id, TransactionDto transactionDto) {
        Optional<Account> accountById = accountRepository.findById(id);

        if (!accountById.isPresent()) {
            throw new ElementNotFoundException("Account with id = " + id + " does not exist!");
        }

        Account account = accountById.get();
        double amount = transactionDto.getAmount();

        if (!transactionDto.getCurrency().equals(account.getCurrency())) {
            amount = CurrencyConverter.convert(transactionDto.getCurrency(), account.getCurrency(), transactionDto.getAmount());
        }

        if (Type.EXPENSE.equals(transactionDto.getType()) && account.getBalance() < amount) {
            throw new InvalidOperationException("Not enough account balance!");
        }

        if (Type.EXPENSE.equals(transactionDto.getType())) {
            account.setBalance(account.getBalance() - amount);
        } else {
            account.setBalance(account.getBalance() + amount);
        }

        accountRepository.save(account);
        return this.transactionService.createTransaction(account.getId(), transactionDto);
    }

    public long addBudget(long id, BudgetDto budgetDto) {
        Account account = getAccountById(id);

        if (account.getBalance() < budgetDto.getAmount()) {
            throw new InvalidOperationException("The budget can not exceed the account balance!");
        }

        return budgetService.createBudget(account.getId(), budgetDto);
    }

    public List<TransactionDto> getTransactionsByType(long id, Type type) {
        return transactionService.getTransactionsByAccountId(id).stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
    }
}
