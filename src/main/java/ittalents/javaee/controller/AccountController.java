package ittalents.javaee.controller;

import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.model.pojo.Type;
import ittalents.javaee.service.AccountService;
import ittalents.javaee.service.BudgetService;
import ittalents.javaee.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
public class AccountController extends AbstractController{

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
    public ResponseEntity getMyAccounts(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<AccountDto> accounts = accountService.getAllAccountsByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}/transfers")
    public ResponseEntity getTransfersByAccountId(@PathVariable @Positive long accountId) {
        List<TransferDto> accounts = accountService.getTransfersByAccountId(accountId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity getTransactionsByAccountId(@PathVariable @Positive long accountId) {
        List<RequestTransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(value = "/accounts/{id}/budgets")
    public ResponseEntity getBudgetsByAccountId(@PathVariable @Positive long accountId){
        List<BudgetDto> budgets = budgetService.getBudgetsByAccountId(accountId);
        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity changeAccountCurrency(@PathVariable @Positive long accountId, @RequestParam Currency currency) {
        AccountDto account = accountService.changeAccountCurrency(accountId, currency).toDto();
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity deleteAccount(@PathVariable @Positive long accountId) {
        this.accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts/makeTransfer")
    public ResponseEntity makeTransfer(@RequestBody @Valid TransferDto transferDto) {
        URI location = URI.create(String.format("/transfers/%d", this.accountService.makeTransfer(transferDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/{id}/makeTransaction")
    public ResponseEntity makeTransaction(@PathVariable @Positive long accountId, @RequestBody @Valid RequestTransactionDto requestTransactionDto) {
        URI location = URI.create(String.format("/transactions/%d", accountService.makeTransaction(accountId, requestTransactionDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/{id}/budgets")
    public ResponseEntity addBudget(@PathVariable @Positive long accountId, @RequestBody @Valid BudgetDto budgetDto){
        URI location = URI.create(String.format("/budgets/%d", this.accountService.addBudget(accountId, budgetDto)));
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/accounts/{id}/transactions/type")
    public ResponseEntity getTransactionsByType(@PathVariable@Positive long accountId, @RequestParam("type") Type type){
        List<RequestTransactionDto> requestTransactionDtos = accountService.getTransactionsByType(accountId, type);
        return ResponseEntity.ok(requestTransactionDtos);
    }
}
