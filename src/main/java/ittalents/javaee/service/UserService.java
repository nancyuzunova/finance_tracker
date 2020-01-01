package ittalents.javaee.service;

import ittalents.javaee.model.User;
import ittalents.javaee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(user);
        }
        return users;
    }

    public User getUserById(long id) {
        return userRepository.findById(id).get();
    }

    public void addUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setDateCreated(now);
        user.setLastLogin(now);
        userRepository.save(user);
    }

    public void editUser(long id, User user) {
        // the repository takes a look if there is a User as row in db,
        // sees its id and if there is row with that user, it overrides it?
        userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public boolean checkUser(long id, String email, String password) {
        if (userRepository.existsById(id)) {
            User user = getUserById(id);
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
