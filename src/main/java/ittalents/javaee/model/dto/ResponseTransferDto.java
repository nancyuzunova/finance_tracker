package ittalents.javaee.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ResponseTransferDto extends AbstractDto{
    private long id;
    private Account fromAccount;
    private Account toAccount;

    @Positive
    private double amount;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date date;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
