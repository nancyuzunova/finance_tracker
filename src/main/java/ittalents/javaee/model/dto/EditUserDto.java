package ittalents.javaee.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditUserDto extends AbstractDto {

    private String firstName;

    private String lastName;
}
