package ittalents.javaee.controller;

import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.SQLException;
import java.util.List;

@RestController
@Validated
public class AccountController extends AbstractController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseEntity getAccounts(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<AccountDto> accounts = accountService.getAllAccountsByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{accountId}/transfers")
    public ResponseEntity getTransfersByAccountId(HttpSession session, @PathVariable @PositiveOrZero long accountId) throws SQLException {
        validateUserOwnership(session, accountId);
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponseTransferDto> transfers = accountService.getTransfersByAccountId(user.getId(), accountId);
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

    @PostMapping("/accounts/makeTransfer")
    public ResponseEntity makeTransfer(HttpSession session, @RequestBody @Valid RequestTransferDto requestTransferDto) {
        validateUserOwnership(session, requestTransferDto.getFromAccountId());
        validateUserOwnership(session, requestTransferDto.getToAccountId());
        ResponseTransferDto transfer = this.accountService.makeTransfer(requestTransferDto);
        return new ResponseEntity(transfer, HttpStatus.CREATED);
    }

    @PostMapping("/accounts/makeTransaction")
    public ResponseEntity makeTransaction(HttpSession session,
                                          @RequestBody @Valid RequestTransactionDto requestTransactionDto) {
        validateUserOwnership(session, requestTransactionDto.getAccountId());
        ResponseTransactionDto transaction = accountService.makeTransaction(requestTransactionDto);
        return new ResponseEntity(transaction, HttpStatus.CREATED);
    }

    @PostMapping("/accounts/makePlannedPayment")
    public ResponseEntity createPlannedPayment(HttpSession session,
                                               @RequestBody @Valid RequestPlannedPaymentDto requestPlannedPaymentDto) {
        validateUserOwnership(session, requestPlannedPaymentDto.getAccountId());
        ResponsePlannedPaymentDto plannedPayment = accountService.createPlannedPayment(requestPlannedPaymentDto);
        return new ResponseEntity(plannedPayment, HttpStatus.CREATED);
    }

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity deleteAccount(HttpSession session, @PathVariable @Positive long accountId) throws SQLException {
        validateUserOwnership(session, accountId);
        AccountDto accountToBeDeleted = accountService.getAccountById(accountId).toDto();
        this.accountService.deleteAccount(accountId);
        return ResponseEntity.ok(accountToBeDeleted);
    }
}
