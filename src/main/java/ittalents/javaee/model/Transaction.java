package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    private int id;
    private Type type;
    private Category category;
    private double amount;
    private LocalDateTime date;
    private int accountId;

    public Transaction(Type type, Category category, double amount, int accountId) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.accountId = accountId;
    }
}
