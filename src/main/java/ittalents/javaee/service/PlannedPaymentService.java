package ittalents.javaee.service;

import ittalents.javaee.Util;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.PlannedPaymentDao;
import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.repository.PlannedPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (Util.MIN_DATE.isAfter(Util.getConvertedDate(paymentDto.getDate())) ||
                Util.MAX_DATE.isBefore(Util.getConvertedDate(paymentDto.getDate()))) {
            throw new InvalidOperationException(Util.INCORRECT_DATES);
        }
        PlannedPayment payment = new PlannedPayment();
        payment.fromDto(paymentDto);
        payment.setAccount(accountService.getAccountById(paymentDto.getAccountId()));
        payment.setCategory(categoryService.getCategoryById(paymentDto.getCategoryId()));
        return paymentRepository.save(payment).toDto();
    }

    public PlannedPayment getPaymentById(long id) {
        Optional<PlannedPayment> x = this.paymentRepository.findById(id);
        if(x.isPresent()){
            return x.get();
        }
        throw new ElementNotFoundException(Util.replacePlaceholder("Planned payment", Util.NOT_FOUND));
    }
}
