package ittalents.javaee.controller;

import ittalents.javaee.model.BudgetDto;
import ittalents.javaee.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
public class BudgetController {

    private BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService){
        this.budgetService = budgetService;
    }

    @GetMapping(value = "/budgets")
    public ResponseEntity getAllBudgets(){
        List<BudgetDto> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping(value = "/budgets/{id}")
    public ResponseEntity getBudgetById(@PathVariable @Positive long id){
        BudgetDto budgetDto = budgetService.getBudgetById(id).toDto();
        return ResponseEntity.ok(budgetDto);
    }

    @DeleteMapping(value = "/budgets/{id}")
    public ResponseEntity deleteBudget(@PathVariable @Positive long id){
        this.budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/budgets/{id}")
    public ResponseEntity changeBudgetAmount(@PathVariable @Positive long id, @RequestParam double amount){
        BudgetDto dto = this.budgetService.changeBudgetAmount(id, amount).toDto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/budgets/{from_date}-{to_date}")
    public ResponseEntity getBudgetsByDateBetween(@PathVariable LocalDate from_date, @PathVariable LocalDate to_date){
        List<BudgetDto> budgetDtos = budgetService.getBudgetsByDate(from_date, to_date);
        return ResponseEntity.ok(budgetDtos);
    }

    @GetMapping(value = "/budgets/before/{date}")
    public ResponseEntity getBudgetsByDateBefore(@PathVariable LocalDate date){
        List<BudgetDto> budgetDtos = budgetService.getBudgetsBefore(date);
        return ResponseEntity.ok(budgetDtos);
    }

    @GetMapping(value = "/budgets/after/{date}")
    public ResponseEntity getBudgetsByDateAfter(@PathVariable LocalDate date){
        List<BudgetDto> budgetDtos = budgetService.getBudgetsAfter(date);
        return ResponseEntity.ok(budgetDtos);
    }
}
