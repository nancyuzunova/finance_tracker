package ittalents.javaee.controller;

import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.pojo.User;
import ittalents.javaee.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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

    @PostMapping("/budgets")
    public ResponseEntity createBudget(HttpSession session, @RequestBody @Valid RequestBudgetDto budgetDto){
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        ResponseBudgetDto budget = budgetService.createBudget(user.getId(), budgetDto).toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @DeleteMapping(value = "/budgets/{budgetId}")
    public ResponseEntity deleteBudget(HttpSession session, @PathVariable @Positive long budgetId) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        ResponseBudgetDto budget = this.budgetService.deleteBudget(budgetId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(budget);
    }

    @PutMapping(value = "/budgets/{budgetId}/amount")
    public ResponseEntity changeBudgetAmount(HttpSession session,
                                             @PathVariable @Positive long budgetId,
                                             @RequestParam("amount") @Positive double amount) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        ResponseBudgetDto dto = this.budgetService.changeBudgetAmount(budgetId, user.getId(), amount).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{budgetId}/category")
    public ResponseEntity changeCategory(HttpSession session,
                                         @PathVariable @Positive long budgetId,
                                         @RequestParam("categoryId") @Positive int categoryId) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        ResponseBudgetDto dto = budgetService.changeBudgetCategory(budgetId, user.getId(), categoryId).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{budgetId}/title")
    public ResponseEntity editTitle(HttpSession session, @PathVariable @Positive long budgetId,
                                    @RequestParam("title") String newTitle) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        ResponseBudgetDto dto = budgetService.changeTitle(budgetId, user.getId(), newTitle).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{budgetId}/period")
    public ResponseEntity updatePeriod(HttpSession session, @PathVariable @Positive long budgetId,
                                       @RequestParam("from") @DateTimeFormat(pattern = "dd.MM.yyyy") Date from,
                                       @RequestParam("to") @DateTimeFormat(pattern = "dd.MM.yyyy") Date to) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        ResponseBudgetDto dto = budgetService.changePeriod(budgetId, user.getId(), from, to).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/budgets/references")
    public ResponseEntity getBudgetReferences(HttpSession session) throws SQLException {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<BudgetService.BudgetStatistics> references = budgetService.getBugetReferences(user.getId());
        return ResponseEntity.ok(references);
    }
}
