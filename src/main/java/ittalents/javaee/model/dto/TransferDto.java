package ittalents.javaee.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransferDto extends AbstractDto{

    private long id;

    @NotNull
    @PositiveOrZero
    private long fromAccountId;

    @NotNull
    @PositiveOrZero
    private long toAccountId;

    @NotNull
    @Positive
    private double amount;

    private LocalDateTime date;
}
