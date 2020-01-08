package ittalents.javaee.controller;

import ittalents.javaee.model.dto.RequestBudgetDto;
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

    @DeleteMapping(value = "/budgets/{id}")
    public ResponseEntity deleteBudget(@PathVariable @Positive long id) {
        this.budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/budgets/{id}")
    public ResponseEntity changeBudgetAmount(@PathVariable @Positive long id, @RequestParam double amount) {
        ResponseBudgetDto dto = this.budgetService.changeBudgetAmount(id, amount).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/budgets/from/to")
    public ResponseEntity getBudgetsByDateBetween(@RequestParam("from")
                                                  @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                                  @RequestParam("to")
                                                  @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {
        List<ResponseBudgetDto> dtos = budgetService.getBudgetsByDate(fromDate, toDate);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/budgets/before")
    public ResponseEntity getBudgetsByDateBefore(@RequestParam("before")
                                                 @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        List<ResponseBudgetDto> dtos = budgetService.getBudgetsBefore(date);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(value = "/budgets/after")
    public ResponseEntity getBudgetsByDateAfter(@RequestParam("after")
                                                @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
        List<ResponseBudgetDto> dtos = budgetService.getBudgetsAfter(date);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping(value = "/budgets/{id}/category/change")
    public ResponseEntity changeCategory(@PathVariable @Positive long id, @RequestParam @Positive long categoryId) {
        ResponseBudgetDto dto = budgetService.changeBudgetCategory(id, categoryId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/title")
    public ResponseEntity editTitle(@PathVariable @Positive long id, @RequestParam String newTitle) {
        ResponseBudgetDto dto = budgetService.changeTitle(id, newTitle).toDto();
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/budgets/{id}/edit/from/{from}/to/{to}")
    public ResponseEntity updatePeriod(@PathVariable @Positive long id, @PathVariable Date from, @PathVariable Date to) {
        ResponseBudgetDto dto = budgetService.changePeriod(id, from, to).toDto();
        return ResponseEntity.ok(dto);
    }
}
