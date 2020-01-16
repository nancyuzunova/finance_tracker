package ittalents.javaee.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestPlannedPaymentDto extends AbstractDto {

    private long id;

    @NotNull
    @Positive
    private double amount;

    @NotNull
    @Positive
    private long accountId;

    @NotNull
    @Positive
    private long categoryId;

    private String title;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private Date date;
}
