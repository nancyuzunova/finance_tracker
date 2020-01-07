package ittalents.javaee.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Transaction;
import ittalents.javaee.model.dto.TransactionDto;
import ittalents.javaee.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private CategoryService categoryService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.categoryService = categoryService;
    }

    public long createTransaction(long accountId, TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.fromDto(transactionDto);
        transaction.setDate(LocalDateTime.now());
        transaction.setAccountId(accountId);
        transaction.setCurrency(transactionDto.getCurrency());
        return this.transactionRepository.save(transaction).getId();
    }

    public List<TransactionDto> getTransactionsByAccountId(long id) {
        return transactionRepository.findAllByAccountId(id).stream().map(Transaction::toDto).collect(Collectors.toList());
    }

    public Transaction getTransactionById(long id) {
        Optional<Transaction> transactionById = transactionRepository.findById(id);

        if (!transactionById.isPresent()) {
            throw new ElementNotFoundException("Transaction with id = " + id + " does not exist!");
        }

        return transactionById.get();
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
            Category category = categoryService.getCategoryById(transaction.getCategoryId());
            document.add(new Chunk(
                    "Category: " + category.getName() + System.lineSeparator(),
                    contentFont));
            document.add(new Chunk("Amount: " + transaction.getAmount() + System.lineSeparator(),
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
