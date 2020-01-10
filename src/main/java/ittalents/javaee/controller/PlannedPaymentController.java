package ittalents.javaee.controller;

import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.service.PlannedPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@RestController
@Validated
public class PlannedPaymentController extends AbstractController {

    @Autowired
    private PlannedPaymentService plannedPaymentService;

    @GetMapping("accounts/{id}/plannedPayments")
    public ResponseEntity getMyPlannedPayments(HttpSession session, @PathVariable long id) throws SQLException {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponsePlannedPaymentDto> plannedPayments = plannedPaymentService.getAllPlannedPaymentsByUserId(user.getId(), id);
        return ResponseEntity.ok(plannedPayments);
    }

    @GetMapping("/plannedPayments/status")
    public ResponseEntity getPlannedPaymentsByStatus(HttpSession session, @RequestParam("status") PlannedPayment.PaymentStatus status){
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponsePlannedPaymentDto> plannedPaymentDtos = plannedPaymentService.getPaymentsByStatus(user.getId(), status);
        return ResponseEntity.ok(plannedPaymentDtos);
    }
}
