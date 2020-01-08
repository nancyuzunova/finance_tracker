package ittalents.javaee.model.pojo;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.dto.RequestPlannedPaymentDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.repository.AccountRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "planned_payments")
public class PlannedPayment extends AbstractPojo<ResponsePlannedPaymentDto, RequestPlannedPaymentDto> {

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

    @Transient
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void fromDto(RequestPlannedPaymentDto dto) {
        this.amount = dto.getAmount();
        Optional<Account> acc = accountRepository.findById(dto.getAccountId());

        if (!acc.isPresent()) {
            throw new ElementNotFoundException("Account can not be found!");
        }

        this.account = acc.get();
        this.title = dto.getTitle();
        this.date = dto.getDate();
    }

    @Override
    public ResponsePlannedPaymentDto toDto() {
        ResponsePlannedPaymentDto responseDto = new ResponsePlannedPaymentDto();
        responseDto.setId(id);
        responseDto.setTitle(title);
        responseDto.setAmount(amount);
        responseDto.setAccount(account);
        responseDto.setDate(date);
        return responseDto;
    }
}
