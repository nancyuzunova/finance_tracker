package ittalents.javaee.service;

import ittalents.javaee.model.dao.PlannedPaymentDao;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.model.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PlannedPaymentService {

    @Autowired
    private PlannedPaymentDao paymentDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;

    public List<ResponsePlannedPaymentDto> getAllPlannedPaymentsByUserId(long userId, long accountId) throws SQLException {
        if(accountId == 0) {
            return paymentDao.getMyPlannedPayments(userId);
        }
        else{
            return paymentDao.getPlannedPaymentsByAccountId(userId, accountId);
        }
    }

    public List<ResponsePlannedPaymentDto> getPaymentsByStatus(long userId, PlannedPayment.PaymentStatus status) throws SQLException {
        return paymentDao.getPlannedPaymentsByStatus(userId, status);
    }

    public void deletePlannedPayment(long userId, long paymentId) throws SQLException {
        paymentDao.deletePayment(userId, paymentId);
    }
}
