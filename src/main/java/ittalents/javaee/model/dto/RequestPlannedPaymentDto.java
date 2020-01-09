package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.PlannedPayment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestPlannedPaymentDto extends AbstractDto {

    private long id;

    @NotNull
    @Positive
    private double amount;

    @NotNull
    @Positive
    private long accountId;

    @NotBlank
    private String title;

    @NotNull
    private Date date;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PlannedPayment.PaymentStatus status;
}