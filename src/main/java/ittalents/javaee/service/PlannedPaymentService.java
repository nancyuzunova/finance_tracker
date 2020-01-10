package ittalents.javaee.service;

import ittalents.javaee.model.dao.PlannedPaymentDao;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PlannedPaymentService {

    @Autowired
    private PlannedPaymentDao paymentDao;

    public List<ResponsePlannedPaymentDto> getAllPlannedPaymentsByUserId(long userId) throws SQLException {
        return paymentDao.getMyPlannedPayments(userId);
    }
}
