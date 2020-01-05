package ittalents.javaee.repository;

import ittalents.javaee.model.Budget;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends CrudRepository<Budget, Long> {

    List<Budget> findAllByFromDateBetween(LocalDate from, LocalDate to);

    List<Budget> findAllByFromDateAfter(LocalDate after);

    List<Budget> findAllByFromDateBefore(LocalDate before);

    List<Budget> findAllByAccountId(long id);
}
