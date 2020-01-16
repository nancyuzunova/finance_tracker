package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Currency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class AccountDto extends AbstractDto {

    private static final long MAX_BALANCE_VALUE = 110000000000L;

    private long id;

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    @Max(MAX_BALANCE_VALUE)
    private double balance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
