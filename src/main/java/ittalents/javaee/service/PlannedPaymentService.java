package ittalents.javaee.service;

import ittalents.javaee.model.dao.PlannedPaymentDao;
import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.model.pojo.User;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PlannedPaymentService {

    @Autowired
    private PlannedPaymentRepository paymentRepository;

    @Autowired
    private PlannedPaymentDao paymentDao;

    @Autowired
    private AccountService accountService;

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

    public ResponsePlannedPaymentDto editPayment(long userId, RequestPlannedPaymentDto paymentDto) {
        PlannedPayment payment = new PlannedPayment();
        payment.fromDto(paymentDto);
        for(AccountDto account : accountService.getAllAccountsByUserId(userId)){
            if(paymentDto.getAccountId() == account.getId()){
                Account account1 = new Account();
                account1.fromDto(account);
                payment.setAccount(account1);
                return paymentRepository.save(payment).toDto();
            }
        }
        return null;
    }
}
