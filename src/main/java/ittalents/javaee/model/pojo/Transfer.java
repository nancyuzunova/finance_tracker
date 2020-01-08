package ittalents.javaee.model.pojo;

import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.RequestTransferDto;
import ittalents.javaee.model.dto.ResponseTransferDto;
import ittalents.javaee.repository.AccountRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer extends AbstractPojo<ResponseTransferDto, RequestTransferDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "from_account_id", referencedColumnName = "id", nullable = false)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id", referencedColumnName = "id", nullable = false)
    private Account toAccount;

    @Column(name = "amount", nullable = false, updatable = false)
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;

    @Autowired
    @Transient
    private AccountRepository accountRepository;

    public void fromDto(RequestTransferDto requestTransferDto) {
        Optional<Account> fromAccountById = accountRepository.findById(requestTransferDto.getFromAccountId());
        Optional<Account> toAccountById = accountRepository.findById(requestTransferDto.getToAccountId());

        if (!fromAccountById.isPresent() || !toAccountById.isPresent()) {
            throw new InvalidOperationException("Account can not be found!");
        }

        this.fromAccount = fromAccountById.get();
        this.toAccount = toAccountById.get();
        this.amount = requestTransferDto.getAmount();
    }

    public ResponseTransferDto toDto() {
        ResponseTransferDto responseTransferDto = new ResponseTransferDto();
        responseTransferDto.setId(id);
        responseTransferDto.setFromAccount(fromAccount);
        responseTransferDto.setToAccount(toAccount);
        responseTransferDto.setAmount(amount);
        responseTransferDto.setDate(date);
        responseTransferDto.setCurrency(currency);
        return responseTransferDto;
    }
}
