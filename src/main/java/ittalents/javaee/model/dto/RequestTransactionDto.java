package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.model.pojo.Type;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class RequestTransactionDto extends AbstractDto{

    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Positive
    private long categoryId;

    @NotNull
    @Positive
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
