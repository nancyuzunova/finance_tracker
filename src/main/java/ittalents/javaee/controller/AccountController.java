package ittalents.javaee.controller;

import ittalents.javaee.model.Account;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountController {

    private static List<Account> accounts = new ArrayList<>();

    @GetMapping("/accounts/{userId}")
    public List<Account> getAllAccounts(@PathVariable long userId) {
        List<Account> userAccounts = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getUser().getId() == userId) {
                userAccounts.add(account);
            }
        }
        return userAccounts;
    }

    @GetMapping("/accounts/{id}")
    public Account getAccountById(@PathVariable long id) {
        for (Account account : accounts) {
            if (account.getId() == id) {
                return account;
            }
        }
        return null;
    }

    @PutMapping("/accounts/{id}")
    public void editAccount(@PathVariable int id, @RequestBody Account account) {
        // TODO
        // AccountService.editAccount(id, account); -> AccountRepository.save(account)
    }

    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable int id) {
        for (Account account : accounts) {
            if (account.getId() == id) {
                accounts.remove(account);
                break;
            }
        }
    }
}
