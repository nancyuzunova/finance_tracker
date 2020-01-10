package ittalents.javaee.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Transaction;
import ittalents.javaee.model.dto.RequestTransactionDto;
import ittalents.javaee.model.pojo.Type;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @AllArgsConstructor
    @Getter
    @Setter
    public
    class ExpenseIncomeEntity{

        private double expense;
        private double income;
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

    public long createTransaction(long accountId, RequestTransactionDto requestTransactionDto) {
        Transaction transaction = new Transaction();
        Category cat = categoryService.getCategoryById(requestTransactionDto.getCategoryId());
        transaction.setCategory(cat);
        transaction.fromDto(requestTransactionDto);
        Optional<Account> acc = accountRepository.findById(accountId);
        if(!acc.isPresent()){
            throw new ElementNotFoundException("Account with id " + accountId + " does NOT exists");
        }
        transaction.setAccount(acc.get());
        return this.transactionRepository.save(transaction).getId();
    }

    public List<ResponseTransactionDto> getTransactionsByAccountId(long id) {
        return transactionRepository.findAllByAccountId(id).stream().map(Transaction::toDto).collect(Collectors.toList());
    }

    public Transaction getTransactionById(long id) {
        Optional<Transaction> transactionById = transactionRepository.findById(id);

        if (!transactionById.isPresent()) {
            throw new ElementNotFoundException("Transaction with id = " + id + " does not exist!");
        }

        return transactionById.get();
    }

    public List<ResponseTransactionDto> getTransactionsByUserId(long userId, long accountId) throws SQLException {
        if(accountId == 0){
            return transactionDao.getMyTransactions(userId);
        }
        else{
            return transactionDao.getTransactionsByAccountId(userId, accountId);
        }
    }

    //TODO finish it
    public Map<LocalDate, ArrayList<ExpenseIncomeEntity>> getDailyStatistics(long id, Date from, Date to) throws SQLException {
        Map<LocalDate, Map<Type, Double>> map = transactionDao.getDailyTransactions(id, from, to);
        Map<LocalDate, ArrayList<ExpenseIncomeEntity>> result = new TreeMap<>();
        for(Map.Entry<LocalDate, Map<Type, Double>> e : map.entrySet()){
            LocalDate date = e.getKey();
            double expense = 0.0;
            double income = 0.0;
            if(e.getValue().get(Type.EXPENSE) != null){
                expense = e.getValue().get(Type.EXPENSE);
            }
            if(e.getValue().get(Type.INCOME) != null){
                income = e.getValue().get(Type.INCOME);
            }
            if(!result.containsKey(date)) {
                result.put(date, new ArrayList<>());
            }
            result.get(date).add(new ExpenseIncomeEntity(expense, income));
        }
        return result;
    }

    public void exportTransactionToPDF(long id) {
        Transaction transaction = getTransactionById(id);

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("transaction.pdf"));
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, 20, BaseColor.BLACK);
            document.add(new Paragraph("Transaction Information" + System.lineSeparator(), titleFont));
            Font contentFont = FontFactory.getFont(FontFactory.TIMES_ITALIC, 15, BaseColor.LIGHT_GRAY);
            document.add(new Chunk(
                    "Type: " + transaction.getType() + System.lineSeparator(),
                    contentFont)); // income or expense
            Category category = categoryService.getCategoryById(transaction.getCategory().getId());
            document.add(new Chunk(
                    "Category: " + category.getName() + System.lineSeparator(),
                    contentFont));
            document.add(new Chunk("Description: " + transaction.getDescription() + System.lineSeparator(),
                    contentFont));
            document.add(new Chunk("Amount: " + transaction.getAmount() + System.lineSeparator(),
                    contentFont));
            document.add(new Chunk("Currency: " + transaction.getCurrency() + System.lineSeparator(),
                    contentFont));
            document.add(new Chunk("Date: " + transaction.getDate() + System.lineSeparator(),
                    contentFont));
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
