package ittalents.javaee.repository;

import ittalents.javaee.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {
    @Override
    List<Account> findAll();
}
