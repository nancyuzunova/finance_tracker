package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Getter
@Setter
public class BudgetDto {

    private long id;
    private LocalDate fromDate;
    private LocalDate toDate;

    @NotNull
    @Positive
    private long categoryId;

    @NotNull
    @PositiveOrZero
    private double amount;

    @NotNull
    @Positive
    private int accountId;

    @NotNull
    private String title;
}
