package ittalents.javaee.controller;

import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.pojo.Type;
import ittalents.javaee.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.PositiveOrZero;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@RestController
@Validated
public class TransactionController extends AbstractController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity getTransactionsByAccountId(HttpSession session, @PathVariable long id) throws SQLException {
        validateUserOwnership(session, id);
        UserDto userDto = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponseTransactionDto> transactions = transactionService.getTransactions(userDto.getId(), id);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/accounts/{accountId}/transactions/type")
    public ResponseEntity getTransactionsByType(HttpSession session,
                                                @PathVariable @PositiveOrZero long accountId,
                                                @RequestParam("type") Type type) throws SQLException {
        validateUserOwnership(session, accountId);
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponseTransactionDto> responseTransactionDtos = transactionService
                .getTransactionsByType(user.getId(), accountId, type);
        return ResponseEntity.ok(responseTransactionDtos);
    }

    @GetMapping("/transactions")
    public ResponseEntity
    getExpensesAndIncomesByDateBetween(HttpSession session,
                                       @RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
                                       @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") Date to)
            throws SQLException {
        UserDto userDto = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<TransactionService.ExpenseIncomeEntity> statisticsPerDay =
                transactionService.getDailyStatistics(userDto.getId(), from, to);
        return ResponseEntity.ok(statisticsPerDay);
    }
}
