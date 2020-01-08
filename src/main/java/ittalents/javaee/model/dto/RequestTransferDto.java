package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Currency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
public class RequestTransferDto extends AbstractDto {

    private long id;

    @NotNull
    @Positive
    private long fromAccountId;

    @NotNull
    @Positive
    private long toAccountId;

    @NotNull
    @Positive
    private double amount;

    private LocalDateTime date;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
