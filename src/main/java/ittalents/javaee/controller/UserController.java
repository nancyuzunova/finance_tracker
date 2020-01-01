package ittalents.javaee.controller;

import ittalents.javaee.model.User;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public void saveUser(@RequestBody User user) {
        if (validateUser(user)) {
            userService.addUser(user);
        } else {
            // throw exception?
            System.out.println("Invalid email or password, try again!");
        }
    }

    private boolean validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().isEmpty()
                || user.getLastName() == null || user.getLastName().isEmpty()) {
            return false;
        }

        boolean validEmail = validateEmail(user.getEmail());
        boolean validPassword = validatePassword(user.getPassword());

        if (validEmail && validPassword) {
            return true;
        }
        return false;
    }

    private boolean validateEmail(String email) {
        // https://emailregex.com/
        String regex = "(?:[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b" +
                "\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9]" +
                "(?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
                "\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f" +
                "\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    private boolean validatePassword(String password) {
        /** https://stackoverflow.com/questions/3802192/regexp-java-for-password-validation
         * password must satisfy the following requirements:
         * at least one digit, lower case, upper case, no whitespaces, minimum 8 symbols long
         */
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
            if (matcher.find()) {
            return true;
        }
        return false;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable() long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public void editUser(@PathVariable long id, @RequestBody User user, HttpServletRequest req) {
        if (SessionManager.validateLogged(req)) {
            userService.editUser(id, user);
        } else {
            System.out.println(SessionManager.EXPIRED_SESSION);
        }
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
