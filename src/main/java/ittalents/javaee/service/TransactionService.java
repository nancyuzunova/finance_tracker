package ittalents.javaee.service;

import ittalents.javaee.model.Transaction;
import ittalents.javaee.model.TransactionDto;
import ittalents.javaee.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(long accountId, TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.fromDto(transactionDto);
        transaction.setDate(LocalDateTime.now());
        transaction.setAccountId(accountId);
        this.transactionRepository.save(transaction);
    }
}
