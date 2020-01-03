package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class TransactionDto {

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
    private long accountId;
}
