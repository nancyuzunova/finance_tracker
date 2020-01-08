package ittalents.javaee.controller;

import ittalents.javaee.exceptions.AuthorizationException;
import ittalents.javaee.model.dto.BudgetDto;
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
        List<BudgetDto> myBudgets = budgetService.getMyBudgets(user.getId());
        return ResponseEntity.ok(myBudgets);
    }

    @GetMapping(value = "/budgets/{id}")
    public ResponseEntity getBudgetById(@PathVariable @Positive long id) {
        BudgetDto budgetDto = budgetService.getBudgetById(id).toDto();
        return ResponseEntity.ok(budgetDto);
    }

    @DeleteMapping(value = "/budgets/{id}")
    public ResponseEntity deleteBudget(@PathVariable @Positive long id) {
        this.budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/budgets/{id}")
    public ResponseEntity changeBudgetAmount(@PathVariable @Positive long id, @RequestParam double amount) {
        BudgetDto dto = this.budgetService.changeBudgetAmount(id, amount).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/budgets/from/to")
    public ResponseEntity getBudgetsByDateBetween(@RequestParam("from")
                                                  @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                                  @RequestParam("to")
                                                  @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        List<BudgetDto> budgetDtos = budgetService.getBudgetsByDate(fromDate, toDate);
        return ResponseEntity.ok(budgetDtos);
    }

    @GetMapping(value = "/budgets/before")
    public ResponseEntity getBudgetsByDateBefore(@RequestParam("before")
                                                 @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        List<BudgetDto> budgetDtos = budgetService.getBudgetsBefore(date);
        return ResponseEntity.ok(budgetDtos);
    }

    @GetMapping(value = "/budgets/after")
    public ResponseEntity getBudgetsByDateAfter(@RequestParam("after")
                                                @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        List<BudgetDto> budgetDtos = budgetService.getBudgetsAfter(date);
        return ResponseEntity.ok(budgetDtos);
    }

    @PutMapping(value = "/budgets/{id}/category/change")
    public ResponseEntity changeCategory(@PathVariable @Positive long id, @RequestParam @Positive long categoryId) {
        BudgetDto dto = budgetService.changeBudgetCategory(id, categoryId).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/title")
    public ResponseEntity editTitle(@PathVariable @Positive long id, @RequestParam String newTitle) {
        BudgetDto dto = budgetService.changeTitle(id, newTitle).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/edit/from/{from}/to/{to}")
    public ResponseEntity updatePeriod(@PathVariable @Positive long id, @PathVariable Date from, @PathVariable Date to) {
        BudgetDto dto = budgetService.changePeriod(id, from, to).toDto();
        return ResponseEntity.ok(dto);
    }
}
