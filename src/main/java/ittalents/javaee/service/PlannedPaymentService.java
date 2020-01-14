package ittalents.javaee.service;

import ittalents.javaee.model.dao.PlannedPaymentDao;
import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlannedPaymentService {

    @Autowired
    private PlannedPaymentRepository paymentRepository;

    @Autowired
    private PlannedPaymentDao paymentDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    public List<ResponsePlannedPaymentDto> getAllPlannedPaymentsByUserId(long userId, long accountId) throws SQLException {
        if (accountId == 0) {
            return paymentDao.getMyPlannedPayments(userId);
        } else {
            List<PlannedPayment> payments = paymentRepository.findAllByAccountId(accountId);
            List<ResponsePlannedPaymentDto> paymentDtos = new ArrayList<>();
            for (PlannedPayment payment : payments) {
                paymentDtos.add(payment.toDto());
            }
            return paymentDtos;
        }
    }

    public List<ResponsePlannedPaymentDto> getPaymentsByStatus(long userId, PlannedPayment.PaymentStatus status) throws SQLException {
        return paymentDao.getPlannedPaymentsByStatus(userId, status);
    }

    public void deletePlannedPayment(long userId, long paymentId) throws SQLException {
        paymentDao.deletePayment(userId, paymentId);
    }

    public ResponsePlannedPaymentDto editPayment(RequestPlannedPaymentDto paymentDto) {
        PlannedPayment payment = new PlannedPayment();
        payment.fromDto(paymentDto);
        payment.setAccount(accountService.getAccountById(paymentDto.getAccountId()));
        payment.setCategory(categoryService.getCategoryById(paymentDto.getCategoryId()));
        return paymentRepository.save(payment).toDto();
    }
}
