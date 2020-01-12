package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Budget;
import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.CurrencyConverter;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.BudgetRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class BudgetStatistics{
        private double total;
        private double spent;
        private double percentage;
        private CategoryDto category;
    }

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

    public List<BudgetStatistics> getBugetReferences(long accountId) throws SQLException {
        Optional<Account> a = accountRepository.findById(accountId);
        if(!a.isPresent()){
            throw new ElementNotFoundException("Account not found!");
        }
        Account account = a.get();
        List<ResponseBudgetDto> budgets = getBudgetsByAccountId(accountId);
        List<TransactionDao.ExpensesByCategoryAndAccount> expenses = transactionDao.getTransactionsAmountByCategory(accountId);
        List<Category.CategoryName> categories = new ArrayList<>();
        List<BudgetStatistics> result = new ArrayList<>();
        for (TransactionDao.ExpensesByCategoryAndAccount e : expenses) {
            categories.add(e.getCategoryDto().getCategoryName());
        }
        for (ResponseBudgetDto budget : budgets) {
            for (int i = 0; i < expenses.size(); i++) {
                TransactionDao.ExpensesByCategoryAndAccount expense = expenses.get(i);
                if (budget.getCategory().getCategoryName().equals(expense.getCategoryDto().getCategoryName())) {
                    //convert currencies
                    int occurrences = Collections.frequency(categories, expense.getCategoryDto().getCategoryName());
                    double spent = 0;
                    if(occurrences > 1){
                        for (int j = 0; j < occurrences; j++) {
                            spent += CurrencyConverter.convert(expense.getCurrency(), account.getCurrency(), expense.getTotalExpenses());
                            i++;
                        }
                    }
                    else{
                        spent = CurrencyConverter.convert(expense.getCurrency(), account.getCurrency(), expense.getTotalExpenses());
                    }
                    double percentage = spent / budget.getAmount() * 100;
                    result.add(new BudgetStatistics(budget.getAmount(), spent, percentage, expense.getCategoryDto()));
                }
            }
        }
        return result;
    }
}
