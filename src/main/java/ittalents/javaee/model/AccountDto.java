package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class AccountDto implements IDto{

    private long id;

    @NotNull
    @PositiveOrZero
    private double balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
