package ittalents.javaee.repository;

import ittalents.javaee.model.Budget;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends CrudRepository<Budget, Long> {

    List<Budget> findAllByFromDateBetween(LocalDate from, LocalDate to);

    List<Budget> findAllByFromDateAfter(LocalDate after);

    List<Budget> findAllByFromDateBefore(LocalDate before);

    List<Budget> findAllByAccountId(long id);
}
