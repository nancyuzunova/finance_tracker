package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findAllByUserId(long id);
}
