package ittalents.javaee.repository;

import ittalents.javaee.model.pojo.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlannedPaymentRepository extends JpaRepository<PlannedPayment, Long> {
}
