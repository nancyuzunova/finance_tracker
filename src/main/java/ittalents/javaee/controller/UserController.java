package ittalents.javaee.controller;

import ittalents.javaee.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {

    private static ArrayList<User> users = new ArrayList<>();
//    static{
//        list.add(new User("Pesho", 13, "petyr@abv.bg", "CSKArulz"));
//        list.add(new User("Tosho", 23, "toshetyyy@abv.bg", "levskiilismyrt"));
//    }

    @GetMapping(value = "/hi")
    public String sayHi() {
        return "Hi from Spring";
    }

    @GetMapping(value = "/users/all")
    public ArrayList<User> getUsers() {
        return users;
    }

    @PostMapping(value = "/users/add")
    public void saveUser(@RequestBody User user) {
        if (validateUser(user)) {
            System.out.println("A user has been added!");
            // add to db
        } else {
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

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") long id) {
        for (User u : users) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    @PutMapping("/users/{id}")
    public void editUser(@PathVariable long id, @RequestBody User user, HttpServletRequest req) {
        if (SessionManager.validateLogged(req)) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == id) {
                    users.set(i, user);
                    break;
                }
            }
        } else {
            System.out.println(SessionManager.EXPIRED_SESSION);
        }
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        for (User user : users) {
            if (user.getId() == id) {
                users.remove(user);
                break;
            }
        }
    }
}
