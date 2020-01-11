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
    public ResponseEntity getAccounts(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<AccountDto> accounts = accountService.getAllAccountsByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{accountId}/transfers")
    public ResponseEntity getTransfersByAccountId(HttpSession session, @PathVariable @Positive long accountId) {
        validateUserOwnership(session, accountId);
        List<ResponseTransferDto> transfers = accountService.getTransfersByAccountId(accountId);
        return ResponseEntity.ok(transfers);
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity changeAccountCurrency(HttpSession session,
                                                @PathVariable @Positive long accountId,
                                                @RequestParam("currency") Currency currency) {
        validateUserOwnership(session, accountId);
        AccountDto account = accountService.changeAccountCurrency(accountId, currency).toDto();
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity deleteAccount(HttpSession session, @PathVariable @Positive long accountId) {
        validateUserOwnership(session, accountId);
        this.accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts/makeTransfer")
    public ResponseEntity makeTransfer(HttpSession session, @RequestBody @Valid RequestTransferDto requestTransferDto) {
        validateUserOwnership(session, requestTransferDto.getFromAccountId());
        validateUserOwnership(session, requestTransferDto.getToAccountId());
        URI location = URI.create(String.format("/transfers/%d", this.accountService.makeTransfer(requestTransferDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/makeTransaction")
    public ResponseEntity makeTransaction(HttpSession session,
                                          @RequestBody @Valid RequestTransactionDto requestTransactionDto) {
        validateUserOwnership(session, requestTransactionDto.getAccountId());
        URI location = URI.create(String.format("/transactions/%d", accountService.makeTransaction(requestTransactionDto)));
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/accounts/budgets")
    public ResponseEntity addBudget(HttpSession session, @RequestBody @Valid RequestBudgetDto requestBudgetDto) {
        validateUserOwnership(session, requestBudgetDto.getAccountId());
        URI location = URI.create(String.format("/budgets/%d", this.accountService.addBudget(requestBudgetDto)));
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/accounts/{accountId}/transactions/type")
    public ResponseEntity getTransactionsByType(HttpSession session,
                                                @PathVariable @Positive long accountId,
                                                @RequestParam("type") Type type) {
        validateUserOwnership(session, accountId);
        List<ResponseTransactionDto> responseTransactionDtos = accountService.getTransactionsByType(accountId, type);
        return ResponseEntity.ok(responseTransactionDtos);
    }

    @PostMapping("/accounts/makePlannedPayment")
    public ResponseEntity createPlannedPayment(HttpSession session,
                                               @RequestBody @Valid RequestPlannedPaymentDto requestPlannedPaymentDto) {
        validateUserOwnership(session, requestPlannedPaymentDto.getAccountId());
        URI location = URI.create(String.format("/plannedPayments/%d",
                accountService.createPlannedPayment(requestPlannedPaymentDto)));
        return ResponseEntity.created(location).build();
    }
}
