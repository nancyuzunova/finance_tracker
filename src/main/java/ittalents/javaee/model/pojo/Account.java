package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.AccountDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "accounts")
public class Account extends AbstractPojo<AccountDto,AccountDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @NotBlank
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @NotNull
    private LocalDateTime createdOn;
    @Positive
    private double balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Override
    public void fromDto(AccountDto dto) {
        this.balance = dto.getBalance();
        this.name = dto.getName();
        if (dto.getCurrency() != null) {
            this.currency = dto.getCurrency();
        }
    }

    @Override
    public AccountDto toDto() {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(id);
        accountDto.setName(name);
        accountDto.setBalance(balance);
        accountDto.setCurrency(currency);
        return accountDto;
    }
}
