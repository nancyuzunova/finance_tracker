package ittalents.javaee.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ResponseBudgetDto extends AbstractDto {

    private long id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date fromDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date toDate;

    @NotNull
    private CategoryDto category;

    @NotNull
    @PositiveOrZero
    private double amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    private String title;
}
