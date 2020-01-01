package ittalents.javaee.service;

import ittalents.javaee.model.Account;
import ittalents.javaee.model.AccountDto;
import ittalents.javaee.model.User;
import ittalents.javaee.repository.AccountRepository;
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

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountDto> getAllAccounts() {
        List<AccountDto> accounts = new ArrayList<>();
        for (Account account : accountRepository.findAll()) {
            accounts.add(account.toDto());
        }
        return accounts;
    }

    public AccountDto getAccountById(long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            return account.get().toDto();
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
}
