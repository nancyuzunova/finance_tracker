package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    void deleteTransactionByAccount_Id(long accountId);

    List<Transaction> findAllByAccountId(long id);
}
