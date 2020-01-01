package ittalents.javaee.controller;

import ittalents.javaee.model.Account;
import ittalents.javaee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts(@PathVariable @PositiveOrZero long userId) {
        return accountService.getAllAccounts();
    }

//    @GetMapping("/accounts/{id}")
//    public Account getAccountById(@PathVariable long id) {
//        for (Account account : accounts) {
//            if (account.getId() == id) {
//                return account;
//            }
//        }
//        return null;
//    }

    @PutMapping("/accounts/{id}")
    public void editAccount(@PathVariable int id, @RequestBody Account account) {
        // TODO
        // AccountService.editAccount(id, account); -> AccountRepository.save(account)
    }

//    @DeleteMapping("/accounts/{id}")
//    public void deleteAccount(@PathVariable int id) {
//        for (Account account : accounts) {
//            if (account.getId() == id) {
//                accounts.remove(account);
//                break;
//            }
//        }
//    }
}
