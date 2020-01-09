package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "planned_payments")
public class PlannedPayment extends AbstractPojo<ResponsePlannedPaymentDto, RequestPlannedPaymentDto> {

    public enum PaymentStatus{
        PAID, ACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private double amount;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @NotNull
    private String title;

    @NotNull
    private Date date;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.ACTIVE;

    @Override
    public void fromDto(RequestPlannedPaymentDto dto) {
        this.amount = dto.getAmount();
        this.title = dto.getTitle();
        this.date = dto.getDate();
        this.status = PaymentStatus.ACTIVE;
    }

    @Override
    public ResponsePlannedPaymentDto toDto() {
        ResponsePlannedPaymentDto responseDto = new ResponsePlannedPaymentDto();
        responseDto.setId(id);
        responseDto.setTitle(title);
        responseDto.setAmount(amount);
        responseDto.setAccount(account);
        responseDto.setDate(date);
        responseDto.setStatus(status);
        return responseDto;
    }
}
