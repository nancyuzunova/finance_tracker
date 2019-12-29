package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Budget {

    private int id;
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
}
