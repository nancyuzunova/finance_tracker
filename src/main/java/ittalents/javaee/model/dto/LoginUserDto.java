package ittalents.javaee.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginUserDto extends AbstractDto {

    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
