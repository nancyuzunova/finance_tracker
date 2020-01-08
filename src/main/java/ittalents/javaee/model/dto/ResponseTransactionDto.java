package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.model.pojo.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTransactionDto extends AbstractDto{

    private long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    private Category categoryId;

    @Positive
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
