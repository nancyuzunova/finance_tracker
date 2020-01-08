package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.AccountDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "accounts")
public class Account extends AbstractPojo<AccountDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User user;
    private LocalDateTime createdOn;
    private double balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Override
    public void fromDto(AccountDto dto) {
        this.balance = dto.getBalance();

        if (dto.getCurrency() != null) {
            this.currency = dto.getCurrency();
        }
    }

    @Override
    public AccountDto toDto() {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(id);
        accountDto.setBalance(balance);
        accountDto.setCurrency(currency);
        return accountDto;
    }
}