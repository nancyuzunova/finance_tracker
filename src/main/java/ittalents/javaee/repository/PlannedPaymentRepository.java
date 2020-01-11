package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PlannedPaymentRepository extends JpaRepository<PlannedPayment, Long> {

    List<PlannedPayment> findAllByDateAndStatus(Date today, PlannedPayment.PaymentStatus status);

    List<PlannedPayment> findAllByAccountId(long accountId);
}
