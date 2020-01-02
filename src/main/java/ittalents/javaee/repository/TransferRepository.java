package ittalents.javaee.repository;

import ittalents.javaee.model.Transfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends CrudRepository<Transfer, Long> {

    List<Transfer> findByFromAccountId(long id);

    List<Transfer> findByToAccountId(long id);
}
