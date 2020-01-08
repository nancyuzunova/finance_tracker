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
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends AbstractPojo<RequestTransactionDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    private Category category;
    private double amount;
    private LocalDateTime date;
    private Account account;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Autowired
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
    }

    public ResponseTransactionDto toDto() {
        ResponseTransactionDto requestTransactionDto = new ResponseTransactionDto();
        requestTransactionDto.setId(id);
        requestTransactionDto.setType(type);
        requestTransactionDto.setCategoryId(category);
        requestTransactionDto.setAmount(amount);
        requestTransactionDto.setCurrency(currency);
        return requestTransactionDto;
    }
}
