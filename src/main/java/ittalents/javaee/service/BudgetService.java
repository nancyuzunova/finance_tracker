package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Budget;
import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.pojo.Category;
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
    private CategoryService categoryService;
    private TransactionDao transactionDao;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, AccountRepository accountRepository,
                         CategoryService categoryService, TransactionDao transactionDao) {
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
        this.categoryService = categoryService;
        this.transactionDao = transactionDao;
    }

    public List<ResponseBudgetDto> getBudgets(long userId) {
        List<Account> accounts = accountRepository.findAllByUserId(userId);
        List<ResponseBudgetDto> budgets = new ArrayList<>();
        for (Account account : accounts) {
            budgets.addAll(budgetRepository.findAllByAccountId(account.getId())
                    .stream().map(Budget::toDto).collect(Collectors.toList()));
        }
        return budgets;
    }

    public Budget getBudgetById(long budgetId) {
        Optional<Budget> budget = budgetRepository.findById(budgetId);
        if (budget.isPresent()) {
            return budget.get();
        }
        throw new ElementNotFoundException("Budget with id " + budgetId + " does NOT exist");
    }

    public void deleteBudget(long budgetId) {
        this.budgetRepository.deleteById(budgetId);
    }

    public Budget changeBudgetAmount(long budgetId, double amount) {
        Budget budget = getBudgetById(budgetId);
        budget.setAmount(amount);
        return this.budgetRepository.save(budget);
    }

    public long createBudget(RequestBudgetDto requestBudgetDto) {
        Budget budget = new Budget();
        Date fromDate = requestBudgetDto.getFromDate();
        Date toDate = requestBudgetDto.getToDate();
        if (fromDate.after(toDate)) {
            throw new InvalidOperationException("You can not create budget!");
        }
        Optional<Account> acc = accountRepository.findById(requestBudgetDto.getAccountId());
        if (!acc.isPresent()) {
            throw new InvalidOperationException("Account cannot be found!");
        }
        budget.setAccount(acc.get());
        Category category = categoryService.getCategoryById(requestBudgetDto.getCategoryId());
        budget.setCategory(category);
        budget.fromDto(requestBudgetDto);
        return this.budgetRepository.save(budget).getId();
    }

    public List<ResponseBudgetDto> getBudgetsByAccountId(long accountId) {
        List<ResponseBudgetDto> budgets = new ArrayList<>();
        for (Budget budget : this.budgetRepository.findAllByAccountId(accountId)) {
            budgets.add(budget.toDto());
        }
        return budgets;
    }

    public ResponseBudgetDto changeBudgetCategory(long budgetId, long categoryId) {
        Budget b = getBudgetById(budgetId);
        Category category = categoryService.getCategoryById(categoryId);
        b.setCategory(category);
        this.budgetRepository.save(b);
        return b.toDto();
    }

    public Budget changeTitle(long budgetId, String newTitle) {
        Budget b = getBudgetById(budgetId);
        b.setTitle(newTitle);
        return this.budgetRepository.save(b);
    }

    public Budget changePeriod(long budgetId, Date from, Date to) {
        if (from.after(to)) {
            throw new InvalidOperationException("You can not change period. Please check dates!");
        }
        Budget b = getBudgetById(budgetId);
        b.setFromDate(from);
        b.setToDate(to);
        return this.budgetRepository.save(b);
    }

    public List getBugetReferences(long accountId) {
        List<ResponseBudgetDto> budgets = getBudgetsByAccountId(accountId);
        List<> expenses = transactionDao.
    }
}
