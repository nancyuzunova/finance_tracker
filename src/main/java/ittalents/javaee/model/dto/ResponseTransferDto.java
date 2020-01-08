package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTransferDto {
    private long id;
    private Account fromAccount;
    private Account toAccount;

    @Positive
    private double amount;
    private LocalDateTime date;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
