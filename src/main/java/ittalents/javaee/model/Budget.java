package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "category_id", nullable = false)
    private long categoryId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "account_id", nullable = false)
    private int accountId;
    private String title;

    public void fromDto(BudgetDto dto) {
        this.accountId = dto.getAccountId();
        this.amount = dto.getAmount();
        this.categoryId = dto.getCategoryId();
        this.fromDate = dto.getFromDate();
        this.toDate = dto.getToDate();
        this.title = dto.getTitle();
    }

    public BudgetDto toDto() {
        BudgetDto dto = new BudgetDto();
        dto.setId(id);
        dto.setAccountId(accountId);
        dto.setAmount(amount);
        dto.setCategoryId(categoryId);
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        dto.setTitle(title);
        return dto;
    }
}

