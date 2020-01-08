package ittalents.javaee.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ittalents.javaee.model.pojo.Currency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Date;

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

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date date;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
}
