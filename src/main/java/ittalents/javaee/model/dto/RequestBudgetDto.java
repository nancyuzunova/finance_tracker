package ittalents.javaee.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

@Getter
@Setter
public class RequestBudgetDto extends AbstractDto {

    private long id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date fromDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date toDate;

    @NotNull
    @Positive
    private long categoryId;

    @NotNull
    @PositiveOrZero
    private double amount;

    private long accountId;

    @NotNull
    private String title;
}
