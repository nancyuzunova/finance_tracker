package ittalents.javaee.service;

import ittalents.javaee.exceptions.InvalidTransferOperationException;
import ittalents.javaee.model.*;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    public List<AccountDto> getAllAccounts() {
        List<AccountDto> accounts = new ArrayList<>();
        for (Account account : accountRepository.findAll()) {
            accounts.add(account.toDto());
        }
        return accounts;
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

    public void deleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }

    public void makeTransfer(TransferDto transferDto) {
        Account accountFrom = new Account();
        Account accountTo = new Account();

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

        double amount = transferDto.getAmount();
        if (accountFrom.getBalance() >= amount) {
            // make transfer
            accountFrom.setBalance(accountFrom.getBalance() - amount);
            accountTo.setBalance(accountTo.getBalance() + amount);

            Transfer transfer = new Transfer();
            transfer.fromDto(transferDto);
            this.transferRepository.save(transfer);
            this.accountRepository.save(accountFrom);
            this.accountRepository.save(accountTo);
        } else {
            throw new InvalidTransferOperationException("Not enough balance!");
        }
    }
}
