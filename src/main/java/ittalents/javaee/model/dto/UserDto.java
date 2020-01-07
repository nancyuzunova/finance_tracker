package ittalents.javaee.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserDto extends LoginUserDto{

    protected long id;
    @NotBlank
    protected String firstName;

    @NotBlank
    protected String lastName;

    @JsonIgnore
    private String password;

    protected LocalDateTime dateCreated;
    protected LocalDateTime lastLogin;
    protected List<AccountDto> accounts;
}
