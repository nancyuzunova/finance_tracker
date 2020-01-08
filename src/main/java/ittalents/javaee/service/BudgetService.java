package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Budget;
import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;
    private AccountRepository accountRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, AccountRepository accountRepository) {
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
    }

    public List<RequestBudgetDto> getMyBudgets(long userId) {
        List<Account> accounts = accountRepository.findAllByUserId(userId);
        List<RequestBudgetDto> budgets = new ArrayList<>();
        for (Account account : accounts) {
            budgets.addAll(budgetRepository.findAllByAccountId(account.getId())
                    .stream().map(Budget::toDto).collect(Collectors.toList()));
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

    public List<RequestBudgetDto> getBudgetsByDate(Date fromDate, Date toDate) {
        List<RequestBudgetDto> requestBudgetDtos = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByFromDateBetween(fromDate, toDate)) {
            requestBudgetDtos.add(budget.toDto());
        }
        return requestBudgetDtos;
    }

    public List<RequestBudgetDto> getBudgetsBefore(Date date) {
        List<RequestBudgetDto> requestBudgetDtos = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByFromDateBefore(date)) {
            requestBudgetDtos.add(budget.toDto());
        }
        return requestBudgetDtos;
    }

    public List<RequestBudgetDto> getBudgetsAfter(Date date) {
        List<RequestBudgetDto> requestBudgetDtos = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByFromDateAfter(date)) {
            requestBudgetDtos.add(budget.toDto());
        }
        return requestBudgetDtos;
    }

    public long createBudget(long accountId, RequestBudgetDto requestBudgetDto) {
        Budget budget = new Budget();

        Date fromDate = requestBudgetDto.getFromDate();
        Date toDate = requestBudgetDto.getToDate();

        if (fromDate.after(toDate)) {
            throw new InvalidOperationException("You can not create budget!");
        }

        budget.fromDto(requestBudgetDto);
        budget.setAccountId(accountId);
        return this.budgetRepository.save(budget).getId();
    }

    public List<ResponseBudgetDto> getBudgetsByAccountId(long id) {
        List<ResponseBudgetDto> budgets = new ArrayList<>();
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
