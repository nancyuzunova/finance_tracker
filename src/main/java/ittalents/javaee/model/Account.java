package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Account {

    private int id;
    private User user;
    private LocalDateTime createdOn;
    private double balance;
    private Currency currency;

    public Account(User user, double balance, Currency currency) {
        this.user = user;
        this.balance = balance;
        this.currency = currency;
        this.createdOn = LocalDateTime.now();
    }
}
