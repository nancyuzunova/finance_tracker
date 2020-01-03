package ittalents.javaee.service;

import ittalents.javaee.exceptions.AccountNotFoundException;
import ittalents.javaee.exceptions.InvalidTransactionOperationException;
import ittalents.javaee.exceptions.InvalidTransferOperationException;
import ittalents.javaee.model.*;
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

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferService transferService, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transferService = transferService;
        this.transactionService = transactionService;
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
        throw new NoSuchElementException();
    }

    public void createAccount(User user, AccountDto accountDto) {
        Account a = new Account();
        a.fromDto(accountDto);
        a.setCreatedOn(LocalDateTime.now());
        a.setUser(user);
        this.accountRepository.save(a);
    }

    public void updateAccount(long id, AccountDto accountDto) {
        // TODO how to update only some columns' values ???
        Account account = accountRepository.getOne(id);
        account.fromDto(accountDto);
        this.accountRepository.save(account);
    }

    public void deleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }

    public void makeTransfer(TransferDto transferDto) {
        Account accountFrom;
        Account accountTo;

        try {
            accountFrom = getAccountById(transferDto.getFromAccountId());
        } catch (NoSuchElementException e) {
            throw new InvalidTransferOperationException(
                    "Account with id " + transferDto.getFromAccountId() + " does not exists!");
        }

        try {
            accountTo = getAccountById(transferDto.getToAccountId());
        } catch (NoSuchElementException e) {
            throw new InvalidTransferOperationException(
                    "Account with id " + transferDto.getToAccountId() + " does not exists!");
        }

        if (accountFrom.getUser().getId() != accountTo.getUser().getId()) {
            throw new InvalidTransferOperationException("You can not make transfer to other users!");
        }

        double amount = transferDto.getAmount();
        if (accountFrom.getBalance() >= amount) {
            // make transfer
            accountFrom.setBalance(accountFrom.getBalance() - amount);
            accountTo.setBalance(accountTo.getBalance() + amount);

            this.transferService.createTransfer(transferDto);
            this.accountRepository.save(accountFrom);
            this.accountRepository.save(accountTo);
        } else {
            throw new InvalidTransferOperationException("Not enough balance!");
        }
    }

    public List<TransferDto> getTransfersByAccountId(long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account with id = " + id + " does not exist!");
        }
        return transferService.getTransfersByAccountId(id);
    }

    public void makeTransaction(long id, TransactionDto transactionDto) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account with id = " + id + " does not exist!");
        }

        Account account = accountRepository.getOne(id);
        double amount = transactionDto.getAmount();

        if (account.getBalance() < amount && transactionDto.getType().name().equals(Type.EXPENSE.name())) {
            throw new InvalidTransactionOperationException("Not enough account balance!");
        }

        if (transactionDto.getType().name().equals(Type.EXPENSE.name())) {
            account.setBalance(account.getBalance() - amount);
        } else {
            account.setBalance(account.getBalance() + amount);
        }

        accountRepository.save(account);
        this.transactionService.createTransaction(account.getId(), transactionDto);
    }
}
