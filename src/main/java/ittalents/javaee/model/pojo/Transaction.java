package ittalents.javaee.model.pojo;

import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.RequestTransactionDto;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.repository.CategoryRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends AbstractPojo<ResponseTransactionDto, RequestTransactionDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;
    @Positive
    private double amount;
    @NotNull
    private Date date;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Autowired
    @Transient
    private CategoryRepository categoryRepository;

    public void fromDto(RequestTransactionDto requestTransactionDto) {
        this.type = requestTransactionDto.getType();
        Optional<Category> cat = categoryRepository.findById(requestTransactionDto.getCategoryId());
        if(!cat.isPresent()){
            throw new InvalidOperationException("No such category!");
        }
        this.category = cat.get();
        this.amount = requestTransactionDto.getAmount();
        this.currency = requestTransactionDto.getCurrency();
        this.date = requestTransactionDto.getDate();
    }

    public ResponseTransactionDto toDto() {
        ResponseTransactionDto responseTransactionDto = new ResponseTransactionDto();
        responseTransactionDto.setId(id);
        responseTransactionDto.setType(type);
        responseTransactionDto.setCategoryId(category);
        responseTransactionDto.setAmount(amount);
        responseTransactionDto.setCurrency(currency);
        responseTransactionDto.setDate(date);
        return responseTransactionDto;
    }
}
