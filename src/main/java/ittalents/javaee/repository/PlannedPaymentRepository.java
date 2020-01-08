package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PlannedPaymentRepository extends JpaRepository<PlannedPayment, Long> {

<<<<<<< HEAD
    List<PlannedPayment> findAllByDateAndStatus(Date today, PlannedPayment.PaymentStatus status);
=======
    List<PlannedPayment> findAllByDate(Date date);
>>>>>>> 20656e5dc3109aea294ea7bdb6fac3e95f4eca4f
}
