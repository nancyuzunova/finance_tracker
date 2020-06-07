package ittalents.javaee.service;

import ittalents.javaee.Util;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.model.pojo.*;
import ittalents.javaee.model.dto.RequestTransactionDto;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

@Service
public class TransactionService {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public
    class ExpenseIncomeEntity {
        private double expense;
        private double income;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class TotalExpenseByDate {
        private double total;
        private Currency currency;
        private CategoryDto category;
    }

    private TransactionRepository transactionRepository;
    private CategoryService categoryService;
    private AccountRepository accountRepository;
    private TransactionDao transactionDao;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService,
                              AccountRepository accountRepository, TransactionDao transactionDao) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
        this.accountRepository = accountRepository;
        this.transactionDao = transactionDao;
    }

    @Transactional
    public ResponseTransactionDto createTransaction(RequestTransactionDto requestTransactionDto) {
        Optional<Account> acc = accountRepository.findById(requestTransactionDto.getAccountId());
        if (!acc.isPresent()) {
            throw new ElementNotFoundException(Util.getNotExistingErrorMessage("Account", requestTransactionDto.getAccountId()));
        }
        Account account = acc.get();
        double amount = requestTransactionDto.getAmount();
        if (!requestTransactionDto.getCurrency().equals(account.getCurrency())) {
            amount = CurrencyConverter.convert(requestTransactionDto.getCurrency(), account.getCurrency(), amount);
        }
        if (Type.EXPENSE.equals(requestTransactionDto.getType()) && account.getBalance() < amount) {
            throw new InvalidOperationException(Util.NOT_ENOUGH_BALANCE);
        }
        if (Type.EXPENSE.equals(requestTransactionDto.getType())) {
            account.setBalance(account.getBalance() - amount);
        } else {
            account.setBalance(account.getBalance() + amount);
        }
        accountRepository.save(account);
        Transaction transaction = new Transaction();
        Category cat = categoryService.getCategoryById(requestTransactionDto.getCategoryId());
        transaction.setCategory(cat);
        transaction.fromDto(requestTransactionDto);
        transaction.setAccount(account);
        return this.transactionRepository.save(transaction).toDto();
    }

    public List<ResponseTransactionDto> getTransactionsByAccountId(long id) {
        List<Transaction> transactionsByAccountId = transactionRepository.findAllByAccountId(id);
        List<ResponseTransactionDto> transactions = new ArrayList<>();
        for (Transaction transaction : transactionsByAccountId) {
            transactions.add(transaction.toDto());
        }
        return transactions;
    }

    public List<ResponseTransactionDto> getTransactions(long userId, long accountId) throws SQLException {
        if (accountId == 0) {
            return transactionDao.getMyTransactions(userId);
        } else {
            return getTransactionsByAccountId(accountId);
        }
    }

    public List<ResponseTransactionDto> getTransactionsByType(long userId, long accountId, Type type) throws SQLException {
        if (accountId == 0) {
            return getAllTransactionsByType(userId, type);
        }
        List<ResponseTransactionDto> transactionsByAccountId = getTransactionsByAccountId(accountId);
        List<ResponseTransactionDto> transactionsByType = new ArrayList<>();
        for (ResponseTransactionDto transaction : transactionsByAccountId) {
            if (transaction.getType().equals(type)) {
                transactionsByType.add(transaction);
            }
        }
        return transactionsByType;
    }

    private List<ResponseTransactionDto> getAllTransactionsByType(long userId, Type type) throws SQLException {
        return transactionDao.getAllTransactionsByType(userId, type);
    }

    public Map<LocalDate, ExpenseIncomeEntity> getDailyStatistics(long id, Date from, Date to, boolean export) throws SQLException {
        validateDateRange(from, to);

        List<TransactionDao.StatisticEntity> entities = transactionDao.getDailyTransactions(id, from, to);
        Map<LocalDate, List<ExpenseIncomeEntity>> map = new TreeMap<>();
        for (int i = 0; i < entities.size(); i++) {
            if (!map.containsKey(entities.get(i).getDate())) {
                map.put(entities.get(i).getDate(), new ArrayList<>());
            }
            ExpenseIncomeEntity row = new ExpenseIncomeEntity();
            double total = CurrencyConverter.convert(entities.get(i).getCurrency(), Currency.BGN, entities.get(i).getTotal());
            if (entities.get(i).getType().equals(Type.EXPENSE)) {
                row.setExpense(total);
            }
            if (entities.get(i).getType().equals(Type.INCOME)) {
                row.setIncome(total);
            }
            map.get(entities.get(i).getDate()).add(row);
        }
        Map<LocalDate, ExpenseIncomeEntity> result = new TreeMap<>();
        for (Map.Entry<LocalDate, List<ExpenseIncomeEntity>> entry : map.entrySet()) {
            double totalExpenseInBGN = 0;
            double totalIncomeInBGN = 0;
            for (ExpenseIncomeEntity t : entry.getValue()) {
                totalExpenseInBGN += t.getExpense();
                totalIncomeInBGN += t.getIncome();
            }
            result.put(entry.getKey(), new ExpenseIncomeEntity(totalExpenseInBGN, totalIncomeInBGN));
        }
        if (export) {
            prepareDailyStatisticForExporting(result);
        }
        return result;
    }

    public List<TotalExpenseByDate> getTotalExpensesByDate(long id, long accountId, Date from, Date to) throws SQLException {
        validateDateRange(from, to);

        List<TotalExpenseByDate> list;
        if (accountId == 0) {
            list = this.transactionDao.getAllTotalExpensesByDate(id, from, to);
        } else {
            list = this.transactionDao.getTotalExpensesByDateFromAccount(accountId, from, to);
        }
        List<Category.CategoryName> categories = new ArrayList<>();
        for (TotalExpenseByDate totalExpenseByDate : list) {
            categories.add(totalExpenseByDate.getCategory().getCategoryName());
        }
        List<TotalExpenseByDate> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TotalExpenseByDate element = list.get(i);
            int occurrences = Collections.frequency(categories, element.getCategory().getCategoryName());
            if (occurrences > 1) {
                double amount = 0;
                for (int j = 0; j < occurrences; j++) {
                    amount += CurrencyConverter.convert(list.get(i).getCurrency(), Currency.BGN, list.get(i).getTotal());
                    i++;
                }
                TotalExpenseByDate converted = new TotalExpenseByDate(amount, Currency.BGN, element.getCategory());
                result.add(converted);
            } else {
                double amount = CurrencyConverter.convert(element.getCurrency(), Currency.BGN, element.getTotal());
                result.add(new TotalExpenseByDate(amount, Currency.BGN, element.getCategory()));
            }
        }
        return result;
    }

    public List<ResponseTransactionDto> getTransactionsByDescription(long userId, String description) throws SQLException {
        return transactionDao.getTransactionsByDescription(userId, description);
    }

    public List<TransactionDao.ExpensesByCategoryAndAccount> getExpensesByCategory(long userId, long accountId) throws SQLException {
        List<TransactionDao.ExpensesByCategoryAndAccount> result = new ArrayList<>();
        List<TransactionDao.ExpensesByCategoryAndAccount> expenses;
        if (accountId == 0) {
            expenses = transactionDao.getExpensesByCategoryForAllAccounts(userId);
        } else {
            expenses = transactionDao.getExpensesByCategoryAndAccountId(accountId);
        }
        List<Category.CategoryName> categories = new ArrayList<>();
        for (TransactionDao.ExpensesByCategoryAndAccount e : expenses) {
            categories.add(e.getCategoryDto().getCategoryName());
        }
        for (int i = 0; i < expenses.size(); i++) {
            TransactionDao.ExpensesByCategoryAndAccount e = expenses.get(i);
            int occurrences = Collections.frequency(categories, e.getCategoryDto().getCategoryName());
            if (occurrences > 1) {
                double total = 0;
                for (int j = 0; j < occurrences; j++) {
                    total += CurrencyConverter.convert(expenses.get(i).getCurrency(), Currency.BGN, expenses.get(i).getTotalExpenses());
                    i++;
                }
                TransactionDao.ExpensesByCategoryAndAccount expense = new TransactionDao.ExpensesByCategoryAndAccount(
                        total, Currency.BGN, e.getCategoryDto()
                );
                result.add(expense);
            } else {
                e.setTotalExpenses(CurrencyConverter.convert(e.getCurrency(), Currency.BGN, e.getTotalExpenses()));
                e.setCurrency(Currency.BGN);
                result.add(e);
            }
        }

        return result;
    }

    public List<TransactionDao.ExpensesByCategoryAndAccount> getTotalExpensesByCategory(long userId) throws SQLException {
        return transactionDao.getExpensesByCategoryForAllAccounts(userId);
    }

    public List<ResponseTransactionDto> getTransactionsByPeriod(long id, Date from, Date to) throws SQLException {
        validateDateRange(from, to);
        return transactionDao.getTransactionsByPeriod(id, from, to);
    }

    public List<ResponseTransactionDto> getTransactionsByCategory(long userId, Category.CategoryName category) throws SQLException {
        return transactionDao.getTransactionsByCategory(userId, category);
    }

    public void deleteTransactionByAccountId(long accountId){
        this.transactionRepository.deleteTransactionByAccount_Id(accountId);
    }

    private void validateDateRange(Date from, Date to) {
        if (from.after(to)) {
            throw new InvalidOperationException(Util.INVALID_DATE_RANGE);
        }
    }

    private void prepareDailyStatisticForExporting(Map<LocalDate, ExpenseIncomeEntity> references) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<LocalDate, ExpenseIncomeEntity> reference : references.entrySet()) {
            sb.append("Date: " + reference.getKey()).append(System.lineSeparator());
            sb.append("Expense: " + reference.getValue().getExpense()).append(" " + Currency.BGN).append(System.lineSeparator());
            sb.append("Income: " + reference.getValue().getIncome()).append(" " + Currency.BGN).append(System.lineSeparator());
            sb.append("---------------------------------------------------").append(System.lineSeparator());
        }
        ExporterToPdf.export(sb.toString(), "Transaction");
    }
}
