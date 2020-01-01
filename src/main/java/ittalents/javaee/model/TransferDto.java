package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class TransferDto {

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
}
