package ittalents.javaee.service;

import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.PlannedPaymentDao;
import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlannedPaymentService {

    private final PlannedPaymentRepository paymentRepository;
    private final PlannedPaymentDao paymentDao;
    private final AccountService accountService;
    private final CategoryService categoryService;

    @Autowired
    public PlannedPaymentService(PlannedPaymentRepository paymentRepository, PlannedPaymentDao paymentDao,
                                 AccountService accountService, CategoryService categoryService) {
        this.paymentRepository = paymentRepository;
        this.paymentDao = paymentDao;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

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
        if (LocalDate.of(1900, 1, 1).isAfter(paymentDto.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate()) ||
                LocalDate.of(2150, 1, 1).isBefore(paymentDto.getDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate())) {
            throw new InvalidOperationException("Incorrect date! Please try again!");
        }
        PlannedPayment payment = new PlannedPayment();
        payment.fromDto(paymentDto);
        payment.setAccount(accountService.getAccountById(paymentDto.getAccountId()));
        payment.setCategory(categoryService.getCategoryById(paymentDto.getCategoryId()));
        return paymentRepository.save(payment).toDto();
    }
}
