package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByFromAccountId(long id);

    List<Transfer> findByToAccountId(long id);
}
