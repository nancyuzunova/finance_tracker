package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class AccountDto {

    private long id;

    @NotNull
    @PositiveOrZero
    private double balance;

    @NotNull
    private Currency currency;
}
