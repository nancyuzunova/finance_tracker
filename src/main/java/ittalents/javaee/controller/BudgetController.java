package ittalents.javaee.controller;

import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@RestController
@Validated
public class BudgetController extends AbstractController {

    private BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/budgets")
    public ResponseEntity getBudgets(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponseBudgetDto> myBudgets = budgetService.getBudgets(user.getId());
        return ResponseEntity.ok(myBudgets);
    }

    @GetMapping(value = "/budgets/{budgetId}")
    public ResponseEntity getBudgetById(@PathVariable @Positive long budgetId) {
        ResponseBudgetDto dto = budgetService.getBudgetById(budgetId).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/accounts/{accountId}/budgets")
    public ResponseEntity getBudgetsByAccountId(HttpSession session, @PathVariable @Positive long accountId) {
        validateUserOwnership(session, accountId);
        List<ResponseBudgetDto> budgets = budgetService.getBudgetsByAccountId(accountId);
        return ResponseEntity.ok(budgets);
    }

    @DeleteMapping(value = "/budgets/{budgetId}")
    public ResponseEntity deleteBudget(HttpSession session, @PathVariable @Positive long budgetId) {
        validateUserOwnership(session, budgetService.getBudgetById(budgetId).getAccount().getId());
        this.budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/budgets/{budgetId}/amount")
    public ResponseEntity changeBudgetAmount(HttpSession session,
                                             @PathVariable @Positive long budgetId,
                                             @RequestParam("amount") @Positive double amount) {
        validateUserOwnership(session, budgetService.getBudgetById(budgetId).getAccount().getId());
        ResponseBudgetDto dto = this.budgetService.changeBudgetAmount(budgetId, amount).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{budgetId}/category")
    public ResponseEntity changeCategory(HttpSession session,
                                         @PathVariable @Positive long budgetId,
                                         @RequestParam("categoryId") @Positive int categoryId) {
        validateUserOwnership(session, budgetService.getBudgetById(budgetId).getAccount().getId());
        ResponseBudgetDto dto = budgetService.changeBudgetCategory(budgetId, categoryId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{budgetId}/title")
    public ResponseEntity editTitle(HttpSession session, @PathVariable @Positive long budgetId,
                                    @RequestParam("title") String newTitle) {
        validateUserOwnership(session, budgetService.getBudgetById(budgetId).getAccount().getId());
        ResponseBudgetDto dto = budgetService.changeTitle(budgetId, newTitle).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{budgetId}/period")
    public ResponseEntity updatePeriod(HttpSession session, @PathVariable @Positive long budgetId,
                                       @RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
                                       @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") Date to) {
        validateUserOwnership(session, budgetService.getBudgetById(budgetId).getAccount().getId());
        ResponseBudgetDto dto = budgetService.changePeriod(budgetId, from, to).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/accounts/{accountId}/budgets/references")
    public ResponseEntity getBudgetReferences(HttpSession session, @PathVariable @Positive long accountId) throws SQLException {
        validateUserOwnership(session, accountId);
        List<BudgetService.BudgetStatistics> references = budgetService.getBugetReferences(accountId);
        return ResponseEntity.ok(references);
    }
}
