package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.BudgetRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private UserService userService;
    private CategoryService categoryService;
    private TransactionService transactionService;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, UserService userService,
                         CategoryService categoryService, TransactionService transactionService) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    public List<ResponseBudgetDto> getBudgets(long userId) {
        List<ResponseBudgetDto> responseBudgets = new ArrayList<>();
        List<Budget> budgets = budgetRepository.findAllByOwnerId(userId);
        for (Budget budget : budgets) {
            responseBudgets.add(budget.toDto());
        }
        return responseBudgets;
    }

    public Budget getBudgetById(long budgetId) {
        Optional<Budget> budget = budgetRepository.findById(budgetId);
        if (budget.isPresent()) {
            return budget.get();
        }
        throw new ElementNotFoundException("Budget with id " + budgetId + " does NOT exist!");
    }

    public ResponseBudgetDto deleteBudget(long budgetId, long userId) {
        Optional<Budget> budgetOptional = budgetRepository.findById(budgetId);
        if(!budgetOptional.isPresent()){
            throw new ElementNotFoundException("Budget with id " + budgetId + " does NOT exist!");
        }
        Budget budget = budgetOptional.get();
        if(budget.getOwner().getId() != userId){
            throw new InvalidOperationException("You can delete only your own budgets!");
        }
        this.budgetRepository.deleteById(budgetId);
        return budget.toDto();
    }

    public Budget changeBudgetAmount(long budgetId, long userId, double amount) {
        Budget budget = getBudgetById(budgetId);
        if(budget.getOwner().getId() != userId){
            throw new InvalidOperationException("You can edit only your own budgets!");
        }
        budget.setAmount(amount);
        return this.budgetRepository.save(budget);
    }

    public Budget createBudget(long userId, RequestBudgetDto requestBudgetDto) {
        Budget budget = new Budget();
        Date fromDate = requestBudgetDto.getFromDate();
        Date toDate = requestBudgetDto.getToDate();
        boolean isInappropriateDateFrom = LocalDate.of(1900, 1, 1)
                .isAfter(fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        boolean isInappropriateDateTo = LocalDate.of(1900, 1, 1)
                .isAfter(toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        if(isInappropriateDateFrom || isInappropriateDateTo){
            throw new InvalidOperationException("Inappropriate dates! Please enter correct dates!");
        }
        if (fromDate.after(toDate)) {
            throw new InvalidOperationException("Date range not valid! Please try again!");
        }
        budget.setOwner(userService.getUserById(userId));
        Category category = categoryService.getCategoryById(requestBudgetDto.getCategoryId());
        budget.setCategory(category);
        budget.fromDto(requestBudgetDto);
        return this.budgetRepository.save(budget);
    }

    public Budget changeBudgetCategory(long budgetId, long userId, long categoryId) {
        Budget budget = getBudgetById(budgetId);
        if(budget.getOwner().getId() != userId){
            throw new InvalidOperationException("You can edit only your own budgets!");
        }
        Category category = categoryService.getCategoryById(categoryId);
        budget.setCategory(category);
        return this.budgetRepository.save(budget);
    }

    public Budget changeTitle(long budgetId, long userId, String newTitle) {
        Budget b = getBudgetById(budgetId);
        if(b.getOwner().getId() != userId){
            throw new InvalidOperationException("You can edit only your own budgets!");
        }
        b.setTitle(newTitle);
        return this.budgetRepository.save(b);
    }

    public Budget changePeriod(long budgetId, long userId, Date from, Date to) {
        if (from.after(to)) {
            throw new InvalidOperationException("You can not change period. Please check dates!");
        }
        Budget b = getBudgetById(budgetId);
        if(b.getOwner().getId() != userId){
            throw new InvalidOperationException("You can edit only your own budgets!");
        }
        b.setFromDate(from);
        b.setToDate(to);
        return this.budgetRepository.save(b);
    }

    public List<BudgetStatistics> getBugetReferences(long userId) throws SQLException {
        List<ResponseBudgetDto> budgets = getBudgets(userId);
        List<TransactionDao.ExpensesByCategoryAndAccount> expenses =
                transactionService.getTotalExpensesByCategory(userId);
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
                            spent += CurrencyConverter.convert(expense.getCurrency(), budget.getCurrency(), expense.getTotalExpenses());
                            i++;
                        }
                    }
                    else{
                        spent = CurrencyConverter.convert(expense.getCurrency(), budget.getCurrency(), expense.getTotalExpenses());
                    }
                    double percentage = spent / budget.getAmount() * 100;
                    result.add(new BudgetStatistics(budget.getAmount(), spent, percentage, expense.getCategoryDto()));
                }
            }
        }
        return result;
    }
}
