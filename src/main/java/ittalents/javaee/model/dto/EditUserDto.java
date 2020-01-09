package ittalents.javaee.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EditUserDto extends AbstractDto {

    private long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
