package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    private LocalDate fromDate;
    private LocalDate toDate;
    private Category category;
    private double amount;
    private int accountId;
    private String title;

    public Budget(String title, LocalDate fromDate, LocalDate toDate, Category category, double amount, int accountId) {
        this.title = title;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.category = category;
        this.amount = amount;
        this.accountId = accountId;
    }

    public void fromDto(BudgetDto dto){
        this.accountId = dto.getAccountId();
        this.amount = dto.getAmount();
        if(dto.getCategory() != null) {
            this.category = dto.getCategory();
        }
        this.fromDate = dto.getFromDate();
        this.toDate = dto.getToDate();
        this.title = dto.getTitle();
    }

    public BudgetDto toDto() {
        BudgetDto dto = new BudgetDto();
        dto.setId(id);
        dto.setAccountId(accountId);
        dto.setAmount(amount);
        dto.setCategory(category);
        dto.setFromDate(fromDate);
        dto.setToDate(toDate);
        dto.setTitle(title);
        return dto;
    }
}

