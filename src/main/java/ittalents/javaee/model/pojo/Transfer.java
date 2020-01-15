package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.RequestTransferDto;
import ittalents.javaee.model.dto.ResponseTransferDto;
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
@Table(name = "transfers")
public class Transfer extends AbstractPojo<RequestTransferDto, ResponseTransferDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id", referencedColumnName = "id")
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id", referencedColumnName = "id")
    private Account toAccount;

    @Column(name = "amount", nullable = false, updatable = false)
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "date", nullable = false, updatable = false)
    private Date date;

    @Override
    public void fromDto(RequestTransferDto requestTransferDto) {
        this.currency = requestTransferDto.getCurrency();
        this.amount = requestTransferDto.getAmount();
        this.date = requestTransferDto.getDate();
    }

    @Override
    public ResponseTransferDto toDto() {
        ResponseTransferDto responseTransferDto = new ResponseTransferDto();
        responseTransferDto.setId(id);
        responseTransferDto.setFromAccount(fromAccount.toDto());
        responseTransferDto.setToAccount(toAccount.toDto());
        responseTransferDto.setAmount(amount);
        responseTransferDto.setDate(date);
        responseTransferDto.setCurrency(currency);
        return responseTransferDto;
    }
}
