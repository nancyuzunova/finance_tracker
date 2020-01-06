package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.Budget;
import ittalents.javaee.model.BudgetDto;
import ittalents.javaee.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<BudgetDto> getAllBudgets() {
        List<BudgetDto> budgets = new ArrayList<>();
        for (Budget budget : budgetRepository.findAll()) {
            budgets.add(budget.toDto());
        }
        return budgets;
    }

    public Budget getBudgetById(long id) {
        Optional<Budget> budget = budgetRepository.findById(id);
        if (budget.isPresent()) {
            return budget.get();
        }
        throw new ElementNotFoundException("Budget with id " + id + " does NOT exist");
    }

    public void deleteBudget(long id) {
        this.budgetRepository.deleteById(id);
    }

    public Budget changeBudgetAmount(long id, double amount) {
        Optional<Budget> budget = budgetRepository.findById(id);
        if (!budget.isPresent()) {
            throw new ElementNotFoundException("Budget with id " + id + " does NOT exists");
        }
        Budget budget1 = budget.get();
        budget1.setAmount(amount);
        return this.budgetRepository.save(budget1);
    }

    public List<BudgetDto> getBudgetsByDate(LocalDate from_date, LocalDate to_date) {
        List<BudgetDto> budgetDtos = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByFromDateBetween(from_date, to_date)) {
            budgetDtos.add(budget.toDto());
        }
        return budgetDtos;
    }

    public List<BudgetDto> getBudgetsBefore(LocalDate date) {
        List<BudgetDto> budgetDtos = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByFromDateBefore(date)) {
            budgetDtos.add(budget.toDto());
        }
        return budgetDtos;
    }

    public List<BudgetDto> getBudgetsAfter(LocalDate date) {
        List<BudgetDto> budgetDtos = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByFromDateAfter(date)) {
            budgetDtos.add(budget.toDto());
        }
        return budgetDtos;
    }

    public long createBudget(long accountId, BudgetDto budgetDto) {
        Budget budget = new Budget();

        Date fromDate = budgetDto.getFromDate();
        Date toDate = budgetDto.getToDate();

        if (fromDate.after(toDate)) {
            throw new InvalidOperationException("You can not create budget!");
        }

        budget.fromDto(budgetDto);
        budget.setAccountId(accountId);
        return this.budgetRepository.save(budget).getId();
    }

    public List<BudgetDto> getBudgetsByAccountId(long id) {
        List<BudgetDto> budgets = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByAccountId(id)) {
            budgets.add(budget.toDto());
        }
        return budgets;
    }

    public Budget changeBudgetCategory(long id, long categoryId) {
        Optional<Budget> budget = this.budgetRepository.findById(id);
        if (!budget.isPresent()) {
            throw new ElementNotFoundException("Budget with id " + id + " does NOT exists");
        }
        Budget b = budget.get();
        b.setCategoryId(categoryId);
        return this.budgetRepository.save(b);
    }

    public Budget changeTitle(long id, String newTitle) {
        Optional<Budget> budget = this.budgetRepository.findById(id);
        if (!budget.isPresent()) {
            throw new ElementNotFoundException("Budget with id " + id + " does NOT exists");
        }
        Budget b = budget.get();
        b.setTitle(newTitle);
        return this.budgetRepository.save(b);
    }

    public Budget changePeriod(long id, Date from, Date to) {
        Optional<Budget> budget = this.budgetRepository.findById(id);
        if (!budget.isPresent()) {
            throw new ElementNotFoundException("Budget with id " + id + " does NOT exists");
        }
        Budget b = budget.get();
        b.setFromDate(from);
        b.setToDate(to);
        return this.budgetRepository.save(b);
    }
}
