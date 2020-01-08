package ittalents.javaee.model.pojo;

import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.dto.ResponseBudgetDto;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.CategoryRepository;
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
@Table(name = "budgets")
public class Budget extends AbstractPojo<ResponseBudgetDto, RequestBudgetDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "from_date", nullable = false)
    private Date fromDate;

    @Column(name = "to_date", nullable = false)
    private Date toDate;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Category category;

    @Column(name = "amount", nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Account account;
    @NotNull
    private String title;

    @Autowired
    @Transient
    private AccountRepository accountRepository;
    @Autowired
    @Transient
    private CategoryRepository categoryRepository;

    public void fromDto(RequestBudgetDto dto) {
        Optional<Account> acc = accountRepository.findById(account.getId());
        if(!acc.isPresent()){
            throw new InvalidOperationException("Account cannot be found!");
        }
        this.account = acc.get();
        this.amount = dto.getAmount();
        Optional<Category> c = categoryRepository.findById(category.getId());
        if(!c.isPresent()){
            throw new InvalidOperationException("No such category!");
        }
        this.category = c.get();
        this.fromDate = dto.getFromDate();
        this.toDate = dto.getToDate();
        this.title = dto.getTitle();
    }

    public ResponseBudgetDto toDto() {
        ResponseBudgetDto dto = new ResponseBudgetDto();
        dto.setId(id);
        dto.setAccount(account);
        dto.setAmount(amount);
        dto.setCategory(category);
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        dto.setTitle(title);
        return dto;
    }
}

