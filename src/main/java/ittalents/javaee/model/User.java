package ittalents.javaee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class User {

    private long id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private LocalDateTime dateCreated;
    private LocalDateTime lastLogin;

    public User(String firstName, String lastName, String password, String email) {
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
        setEmail(email);
    }
}
