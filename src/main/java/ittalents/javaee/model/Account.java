package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User user;
    private LocalDateTime createdOn;
    private double balance;
    private Currency currency;

    public void fromDto(AccountDto accountDto) {
        this.balance = accountDto.getBalance();
        this.currency = accountDto.getCurrency();
    }

    public AccountDto toDto() {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(id);
        accountDto.setBalance(balance);
        accountDto.setCurrency(currency);
        return accountDto;
    }
}
