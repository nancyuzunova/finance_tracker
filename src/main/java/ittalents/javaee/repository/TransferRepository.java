package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    void deleteByToAccount_IdOrFromAccount_Id(long toAccountId, long fromAccountId);

    List<Transfer> findAllByFromAccountId(long id);

    List<Transfer> findAllByToAccountId(long id);
}
