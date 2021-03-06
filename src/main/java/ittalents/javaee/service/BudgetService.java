package ittalents.javaee.service;

import ittalents.javaee.Util;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.repository.BudgetRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.*;

@Service
public class BudgetService {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class BudgetStatistics {
        private double total;
        private double spent;
        private Currency currency;
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
        throw new ElementNotFoundException(Util.getNotExistingErrorMessage("Budget", budgetId));
    }

    public ResponseBudgetDto deleteBudget(long budgetId, long userId) {
        Optional<Budget> budgetOptional = budgetRepository.findById(budgetId);
        if (!budgetOptional.isPresent()) {
            throw new ElementNotFoundException(Util.getNotExistingErrorMessage("Budget", budgetId));
        }
        Budget budget = budgetOptional.get();
        validateOwnership(userId, budget, "delete");
        this.budgetRepository.deleteById(budgetId);
        return budget.toDto();
    }

    public Budget changeBudgetAmount(long budgetId, long userId, double amount) {
        Budget budget = getBudgetById(budgetId);
        validateOwnership(userId, budget, "edit");
        budget.setAmount(amount);
        return this.budgetRepository.save(budget);
    }

    public Budget createBudget(long userId, RequestBudgetDto requestBudgetDto) {
        Date fromDate = requestBudgetDto.getFromDate();
        Date toDate = requestBudgetDto.getToDate();
        validateDates(fromDate, toDate);
        Budget budget = new Budget();
        budget.setOwner(userService.getUserById(userId));
        Category category = categoryService.getCategoryById(requestBudgetDto.getCategoryId());
        budget.setCategory(category);
        budget.fromDto(requestBudgetDto);
        return this.budgetRepository.save(budget);
    }

    public Budget changeBudgetCategory(long budgetId, long userId, long categoryId) {
        Budget budget = getBudgetById(budgetId);
        validateOwnership(userId, budget, "edit");
        Category category = categoryService.getCategoryById(categoryId);
        budget.setCategory(category);
        return this.budgetRepository.save(budget);
    }

    public Budget changeTitle(long budgetId, long userId, String newTitle) {
        Budget budget = getBudgetById(budgetId);
        validateOwnership(userId, budget, "edit");
        budget.setTitle(newTitle);
        return this.budgetRepository.save(budget);
    }

    public Budget changePeriod(long budgetId, long userId, Date from, Date to) {
        validateDates(from, to);
        Budget budget = getBudgetById(budgetId);
        validateOwnership(userId, budget, "edit");
        budget.setFromDate(from);
        budget.setToDate(to);
        return this.budgetRepository.save(budget);
    }

    public List<BudgetStatistics> getBudgetReferences(long userId, boolean export) throws SQLException {
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
                    if (occurrences > 1) {
                        for (int j = 0; j < occurrences; j++) {
                            spent += CurrencyConverter.convert(expense.getCurrency(), budget.getCurrency(), expense.getTotalExpenses());
                            i++;
                        }
                    } else {
                        spent = CurrencyConverter.convert(expense.getCurrency(), budget.getCurrency(), expense.getTotalExpenses());
                    }
                    double percentage = spent / budget.getAmount() * 100;
                    result.add(new BudgetStatistics(budget.getAmount(), spent, budget.getCurrency(), percentage, expense.getCategoryDto()));
                }
            }
        }
        if(export) {
            prepareReferenceForExporting(result);
        }
        return result;
    }

    private void validateOwnership(long userId, Budget budget, String operation) {
        if (budget.getOwner().getId() != userId) {
            throw new InvalidOperationException(Util.replacePlaceholder(operation, Util.FOREIGN_BUDGET_OPERATION));
        }
    }

    private void prepareReferenceForExporting(List<BudgetStatistics> references) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= references.size(); i++) {
            BudgetStatistics reference = references.get(i - 1);
            sb.append("Budget: " + i).append(System.lineSeparator());
            sb.append("Total: " + reference.getTotal()).append(" "+reference.getCurrency()).append(System.lineSeparator());
            sb.append("Spent: " + reference.getSpent()).append(" "+reference.getCurrency()).append(System.lineSeparator());
            sb.append(String.format("Percentage: %.2f", reference.getPercentage())).append("%").append(System.lineSeparator());
            sb.append("Category: " + reference.getCategory().getCategoryName()).append(System.lineSeparator());
            sb.append("---------------------------------------------------").append(System.lineSeparator());
        }
        ExporterToPdf.export(sb.toString(), "Budgets");
    }

    private void validateDates(Date fromDate, Date toDate) {
        if (fromDate.after(toDate)) {
            throw new InvalidOperationException(Util.INVALID_DATE_RANGE);
        }
        boolean isTooPastFromDate = Util.MIN_DATE
                .isAfter(Util.getConvertedDate(fromDate));
        boolean isTooPastToDate = Util.MIN_DATE
                .isAfter(Util.getConvertedDate(toDate));
        boolean isTooFutureFromDate = Util.MAX_DATE
                .isBefore(Util.getConvertedDate(fromDate));
        boolean isTooFutureToDate = Util.MAX_DATE
                .isBefore(Util.getConvertedDate(toDate));
        if (isTooPastFromDate || isTooPastToDate || isTooFutureFromDate || isTooFutureToDate) {
            throw new InvalidOperationException(Util.INCORRECT_DATES);
        }
    }
}
