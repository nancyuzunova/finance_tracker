package ittalents.javaee.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginUserDto {

    @NotBlank
    private String email;
    @NotBlank
    private String password;

}
