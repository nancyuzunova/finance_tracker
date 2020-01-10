package ittalents.javaee.controller;

import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.model.pojo.Type;
import ittalents.javaee.service.AccountService;
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
public class AccountController extends AbstractController {

    private AccountService accountService;
    private TransactionService transactionService;

    @Autowired
    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/accounts")
    public ResponseEntity getMyAccounts(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<AccountDto> accounts = accountService.getAllAccountsByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}/transfers")
    public ResponseEntity getTransfersByAccountId(@PathVariable @Positive long accountId) {
        List<ResponseTransferDto> accounts = accountService.getTransfersByAccountId(accountId);
        return ResponseEntity.ok(accounts);
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
    public ResponseEntity makeTransfer(@RequestBody @Valid RequestTransferDto requestTransferDto) {
        URI location = URI.create(String.format("/transfers/%d", this.accountService.makeTransfer(requestTransferDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/makeTransaction")
    public ResponseEntity makeTransaction(@RequestBody @Valid RequestTransactionDto requestTransactionDto) {
        URI location = URI.create(String.format("/transactions/%d", accountService.makeTransaction(requestTransactionDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/budgets")
    public ResponseEntity addBudget(@RequestBody @Valid RequestBudgetDto requestBudgetDto) {
        URI location = URI.create(String.format("/budgets/%d", this.accountService.addBudget(requestBudgetDto)));
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/accounts/{id}/transactions/type")
    public ResponseEntity getTransactionsByType(@PathVariable @Positive long accountId, @RequestParam("type") Type type) {
        List<ResponseTransactionDto> responseTransactionDtos = accountService.getTransactionsByType(accountId, type);
        return ResponseEntity.ok(responseTransactionDtos);
    }

    @PostMapping("/accounts/makePlannedPayment")
    public ResponseEntity createPlannedPayment(@RequestBody @Valid RequestPlannedPaymentDto dto) {
        URI location = URI.create(String.format("/plannedPayments/%d", accountService.createPlannedPayment(dto)));
        return ResponseEntity.created(location).build();
    }
}
