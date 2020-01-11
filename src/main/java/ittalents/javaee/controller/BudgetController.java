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
    public ResponseEntity getMyBudgets(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponseBudgetDto> myBudgets = budgetService.getMyBudgets(user.getId());
        return ResponseEntity.ok(myBudgets);
    }

    @GetMapping(value = "/budgets/{id}")
    public ResponseEntity getBudgetById(@PathVariable @Positive long id) {
        ResponseBudgetDto dto = budgetService.getBudgetById(id).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/accounts/{accountId}/budgets")
    public ResponseEntity getBudgetsByAccountId(HttpSession session, @PathVariable@Positive long accountId) {
        validateUserOwnership(session, accountId);
        List<ResponseBudgetDto> budgets = budgetService.getBudgetsByAccountId(accountId);
        return ResponseEntity.ok(budgets);
    }

    @DeleteMapping(value = "/budgets/{id}")
    public ResponseEntity deleteBudget(HttpSession session, @PathVariable @Positive long id) {
        validateUserOwnership(session, budgetService.getBudgetById(id).getAccount().getId());
        this.budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/budgets/{id}")
    public ResponseEntity changeBudgetAmount(HttpSession session, @PathVariable @Positive long id, @RequestParam double amount) {
        validateUserOwnership(session, budgetService.getBudgetById(id).getAccount().getId());
        ResponseBudgetDto dto = this.budgetService.changeBudgetAmount(id, amount).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/category/change")
    public ResponseEntity changeCategory(HttpSession session, @PathVariable @Positive long id, @RequestParam @Positive long categoryId) {
        validateUserOwnership(session, budgetService.getBudgetById(id).getAccount().getId());
        ResponseBudgetDto dto = budgetService.changeBudgetCategory(id, categoryId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/title")
    public ResponseEntity editTitle(HttpSession session, @PathVariable @Positive long id, @RequestParam String newTitle) {
        validateUserOwnership(session, budgetService.getBudgetById(id).getAccount().getId());
        ResponseBudgetDto dto = budgetService.changeTitle(id, newTitle).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/edit/from/{from}/to/{to}")
    public ResponseEntity updatePeriod(HttpSession session, @PathVariable @Positive long id, @PathVariable Date from, @PathVariable Date to) {
        validateUserOwnership(session, budgetService.getBudgetById(id).getAccount().getId());
        ResponseBudgetDto dto = budgetService.changePeriod(id, from, to).toDto();
        return ResponseEntity.ok(dto);
    }
}
