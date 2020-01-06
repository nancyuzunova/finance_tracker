package ittalents.javaee.repository;

import ittalents.javaee.model.Budget;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BudgetRepository extends CrudRepository<Budget, Long> {

    List<Budget> findAllByFromDateBetween(Date from, Date to);

    List<Budget> findAllByFromDateAfter(Date after);

    List<Budget> findAllByFromDateBefore(Date before);

    List<Budget> findAllByAccountId(long id);
}
