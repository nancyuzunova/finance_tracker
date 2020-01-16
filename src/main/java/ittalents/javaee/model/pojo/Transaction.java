package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.RequestTransactionDto;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends AbstractPojo<RequestTransactionDto, ResponseTransactionDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Positive
    private double amount;
    @NotNull
    private Date date;

    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Override
    public void fromDto(RequestTransactionDto requestTransactionDto) {
        this.type = requestTransactionDto.getType();
        this.amount = requestTransactionDto.getAmount();
        this.currency = requestTransactionDto.getCurrency();
        this.date = requestTransactionDto.getDate();
        this.description = requestTransactionDto.getDescription();
    }

    @Override
    public ResponseTransactionDto toDto() {
        ResponseTransactionDto responseTransactionDto = new ResponseTransactionDto();
        responseTransactionDto.setId(id);
        responseTransactionDto.setType(type);
        responseTransactionDto.setCategory(category.toDto());
        responseTransactionDto.setAmount(amount);
        responseTransactionDto.setCurrency(currency);
        responseTransactionDto.setDate(date);
        responseTransactionDto.setAccount(account.toDto());
        responseTransactionDto.setDescription(description);
        return responseTransactionDto;
    }
}
