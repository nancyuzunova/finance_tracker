package ittalents.javaee.controller;

import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.service.PlannedPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        validateUserOwnership(session, id);
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponsePlannedPaymentDto> plannedPayments = plannedPaymentService.getAllPlannedPaymentsByUserId(user.getId(), id);
        return ResponseEntity.ok(plannedPayments);
    }

    @GetMapping("/plannedPayments/status")
    public ResponseEntity getPlannedPaymentsByStatus(HttpSession session, @RequestParam("status") PlannedPayment.PaymentStatus status) throws SQLException {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        List<ResponsePlannedPaymentDto> plannedPaymentDtos = plannedPaymentService.getPaymentsByStatus(user.getId(), status);
        return ResponseEntity.ok(plannedPaymentDtos);
    }

    @PutMapping("/plannedPayments")
    public ResponseEntity editPlannedPayment(HttpSession session, @RequestBody RequestPlannedPaymentDto paymentDto){
        validateUserOwnership(session, paymentDto.getAccountId());
        ResponsePlannedPaymentDto response = plannedPaymentService.editPayment(paymentDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/plannedPayments/{id}")
    public ResponseEntity deletePlannedPayment(HttpSession session, @PathVariable long id) throws SQLException {
        UserDto dto = (UserDto) session.getAttribute(SessionManager.LOGGED);
        plannedPaymentService.deletePlannedPayment(dto.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
