package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
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
