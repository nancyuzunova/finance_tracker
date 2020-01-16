package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.RequestBudgetDto;
import ittalents.javaee.model.dto.ResponseBudgetDto;
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
@Table(name = "budgets")
public class Budget extends AbstractPojo<RequestBudgetDto, ResponseBudgetDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "from_date", nullable = false)
    private Date fromDate;

    @Column(name = "to_date", nullable = false)
    private Date toDate;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "amount", nullable = false)
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User owner;

    private String title;

    @Override
    public void fromDto(RequestBudgetDto dto) {
        this.amount = dto.getAmount();
        this.fromDate = dto.getFromDate();
        this.toDate = dto.getToDate();
        this.title = dto.getTitle();
        this.currency = dto.getCurrency();
    }

    @Override
    public ResponseBudgetDto toDto() {
        ResponseBudgetDto dto = new ResponseBudgetDto();
        dto.setId(id);
        dto.setAmount(amount);
        dto.setCategory(category.toDto());
        dto.setCurrency(currency);
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        dto.setTitle(title);
        return dto;
    }
}

