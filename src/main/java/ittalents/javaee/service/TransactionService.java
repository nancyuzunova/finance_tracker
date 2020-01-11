package ittalents.javaee.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.dao.TransactionDao;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Transaction;
import ittalents.javaee.model.dto.RequestTransactionDto;
import ittalents.javaee.model.pojo.Type;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

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
        if (!acc.isPresent()) {
            throw new ElementNotFoundException("Account with id " + accountId + " does NOT exists");
        }
        transaction.setAccount(acc.get());
        return this.transactionRepository.save(transaction).getId();
    }

    public List<ResponseTransactionDto> getTransactionsByAccountId(long id) {
        List<Transaction> transactionsByAccountId = transactionRepository.findAllByAccountId(id);
        List<ResponseTransactionDto> transactions = new ArrayList<>();
        for (Transaction transaction : transactionsByAccountId) {
            transactions.add(transaction.toDto());
        }
        return transactions;
    }

    public Transaction getTransactionById(long id) {
        Optional<Transaction> transactionById = transactionRepository.findById(id);

        if (!transactionById.isPresent()) {
            throw new ElementNotFoundException("Transaction with id = " + id + " does not exist!");
        }

        return transactionById.get();
    }


    public Map<LocalDate, Map<Double, Double>> getDailyStatistics(long id, Date from, Date to) throws SQLException {
        Map<Date, Map<Type, Double>> map = transactionDao.getDailyTransactions(id, from, to);
        Map<LocalDate, Map<Double, Double>> result = new TreeMap<>();
        LocalDate start = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
        for (; start.isBefore(end); start = start.plusDays(1)) {
            result.put(start, new HashMap<>());
            result.get(start).put(0.0, 0.0);
        }
        for (Map.Entry<Date, Map<Type, Double>> entry : map.entrySet()) {
            LocalDate date = entry.getKey().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            result.get(date).put(entry.getValue().get(Type.EXPENSE), entry.getValue().get(Type.INCOME));
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
