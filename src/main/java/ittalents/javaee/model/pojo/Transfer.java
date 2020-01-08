package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.TransferDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer extends AbstractPojo<TransferDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    // TODO
    @Column(name = "from_account_id", nullable = false, updatable = false)
    private long fromAccountId;

    //TODO
    @Column(name = "to_account_id", nullable = false, updatable = false)
//    @OneToMany
//    @JoinColumn(name = "to_account_id")
    private long toAccountId;

    @Column(name = "amount", nullable = false, updatable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    public void fromDto(TransferDto transferDto) {
        this.fromAccountId = transferDto.getFromAccountId();
        this.toAccountId = transferDto.getToAccountId();
        this.amount = transferDto.getAmount();
    }

    public TransferDto toDto() {
        TransferDto transferDto = new TransferDto();
        transferDto.setId(id);
        transferDto.setFromAccountId(fromAccountId);
        transferDto.setToAccountId(toAccountId);
        transferDto.setAmount(amount);
        transferDto.setDate(date);
        transferDto.setCurrency(currency);
        return transferDto;
    }
}
