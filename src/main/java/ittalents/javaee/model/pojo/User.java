package ittalents.javaee.model.pojo;

import ittalents.javaee.model.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users", indexes = @Index(columnList = "email", name = "emailIdx"))
public class User extends AbstractPojo<UserDto, UserDto> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotNull
    private LocalDateTime dateCreated;
    @NotNull
    private LocalDateTime lastLogin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private List<Account> accounts = new ArrayList<>();

    @Override
    public void fromDto(UserDto userDto) {
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
        this.password = userDto.getPassword();
        this.email = userDto.getEmail();
        if (userDto.getAccounts() != null) {
            this.accounts = userDto.getAccounts().stream().map(x -> {
                Account account = new Account();
                account.fromDto(x);
                return account;
            }).collect(Collectors.toList());
        }
    }

    @Override
    public UserDto toDto() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setDateCreated(dateCreated);
        userDto.setLastLogin(lastLogin);
        userDto.setAccounts(this.accounts.stream().map(Account::toDto).collect(Collectors.toList()));
        return userDto;
    }
}
