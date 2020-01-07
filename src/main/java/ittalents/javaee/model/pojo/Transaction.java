package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.TransactionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends AbstractPojo<TransactionDto>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    private long categoryId;
    private double amount;
    private LocalDateTime date;
    private long accountId;

    public void fromDto(TransactionDto transactionDto) {
        this.type = transactionDto.getType();
        this.categoryId = transactionDto.getCategoryId();
        this.amount = transactionDto.getAmount();
    }

    public TransactionDto toDto() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(id);
        transactionDto.setType(type);
        transactionDto.setCategoryId(categoryId);
        transactionDto.setAmount(amount);
        return transactionDto;
    }
}
