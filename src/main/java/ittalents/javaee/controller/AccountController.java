package ittalents.javaee.controller;

import ittalents.javaee.model.AccountDto;
import ittalents.javaee.model.TransferDto;
import ittalents.javaee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/accounts/{id}")
    public AccountDto getAccountById(@PathVariable @PositiveOrZero long id) {
        return accountService.getAccountById(id).toDto();
    }

    @PutMapping("/accounts/{id}")
    public void updateAccount(@PathVariable int id, @RequestBody AccountDto accountDto) {
        // TODO
        // AccountService.editAccount(id, account); -> AccountRepository.save(account)
    }

    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable long id) {
        this.accountService.deleteAccount(id);
    }

    @PostMapping("/makeTransfer")
    public void makeTransfer(@RequestBody @Valid TransferDto transferDto) {
        this.accountService.makeTransfer(transferDto);
    }

    // getTransfersByAccountId() ?
}
