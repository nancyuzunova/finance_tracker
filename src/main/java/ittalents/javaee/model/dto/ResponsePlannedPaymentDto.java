package ittalents.javaee.model.dto;

import ittalents.javaee.model.pojo.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePlannedPaymentDto extends AbstractDto{

    private long id;

    @NotNull
    @Positive
    private double amount;

    @NotNull
    private Account account;

    @NotBlank
    private String title;

    @NotNull
    private Date date;
}
