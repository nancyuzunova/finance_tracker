package ittalents.javaee.repository;

import ittalents.javaee.model.Transaction;
import ittalents.javaee.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccountId(long id);

    List<Transaction> findAllByType(Type type);

    List<Transaction> findAllByDateBetween(LocalDateTime from, LocalDateTime to);

    List<Transaction> findAllByCategoryId(long categoryId);
}
