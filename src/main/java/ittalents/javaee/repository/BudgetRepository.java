package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.Budget;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends CrudRepository<Budget, Long> {

    List<Budget> findAllByOwnerId(long id);
}
