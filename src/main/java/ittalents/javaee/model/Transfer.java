package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long fromAccountId;
    private long toAccountId;
    private double amount;

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
        return transferDto;
    }
}
