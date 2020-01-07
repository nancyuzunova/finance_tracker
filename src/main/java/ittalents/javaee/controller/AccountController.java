package ittalents.javaee.controller;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.BudgetDto;
import ittalents.javaee.model.dto.TransactionDto;
import ittalents.javaee.model.dto.TransferDto;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.service.AccountService;
import ittalents.javaee.service.BudgetService;
import ittalents.javaee.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
public class AccountController {

    private AccountService accountService;
    private TransactionService transactionService;
    private BudgetService budgetService;

    @Autowired
    public AccountController(AccountService accountService, TransactionService transactionService, BudgetService budgetService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    @GetMapping("/accounts")
    public ResponseEntity getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity getAccountById(@PathVariable @Positive long id) {
        AccountDto accountDto = accountService.getAccountById(id).toDto();
        return ResponseEntity.ok(accountDto);
    }

    @GetMapping("/accounts/{id}/transfers")
    public ResponseEntity getTransfersByAccountId(@PathVariable @Positive long id) {
        List<TransferDto> accounts = accountService.getTransfersByAccountId(id);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity getTransactionsByAccountId(@PathVariable @Positive long id) {
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(id);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(value = "/accounts/{id}/budgets")
    public ResponseEntity getBudgetsByAccountId(@PathVariable @Positive long id){
        List<BudgetDto> budgets = budgetService.getBudgetsByAccountId(id);
        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity changeAccountCurrency(@PathVariable @Positive long id, @RequestParam Currency currency) {
        AccountDto account = accountService.changeAccountCurrency(id, currency).toDto();
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity deleteAccount(@PathVariable @Positive long id) {
        this.accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts/makeTransfer")
    public ResponseEntity makeTransfer(@RequestBody @Valid TransferDto transferDto) {
        URI location = URI.create(String.format("/transfers/%d", this.accountService.makeTransfer(transferDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/{id}/makeTransaction")
    public ResponseEntity makeTransaction(@PathVariable @Positive long id, @RequestBody @Valid TransactionDto transactionDto) {
        URI location = URI.create(String.format("/transactions/%d", accountService.makeTransaction(id, transactionDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/{id}/budgets")
    public ResponseEntity addBudget(@PathVariable @Positive long id, @RequestBody @Valid BudgetDto budgetDto){
        URI location = URI.create(String.format("/budgets/%d", this.accountService.addBudget(id, budgetDto)));
        return ResponseEntity.created(location).build();
    }
}
