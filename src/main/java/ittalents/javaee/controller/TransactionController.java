package ittalents.javaee.controller;

import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@RestController
@Validated
public class TransactionController extends AbstractController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transactions/expenses/incomes/days")
    public ResponseEntity getExpensesAndIncomesByDateBetween(HttpSession session, @RequestParam("from")
    @DateTimeFormat(pattern = "dd.MM.yyyy") Date from, @RequestParam("to")
    @DateTimeFormat(pattern = "dd.MM.yyyy") Date to)
            throws SQLException {
        UserDto userDto = (UserDto) session.getAttribute(SessionManager.LOGGED);
        Map<LocalDate, ArrayList<TransactionService.ExpenseIncomeEntity>> statisticsPerDay =
                transactionService.getDailyStatistics(userDto.getId(), from, to);
        return ResponseEntity.ok(statisticsPerDay);
    }
}
