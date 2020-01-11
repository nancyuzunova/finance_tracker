package ittalents.javaee.service;

import ittalents.javaee.exceptions.AuthorizationException;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.*;
import ittalents.javaee.model.pojo.User;
import ittalents.javaee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private AccountService accountService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    public User getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ElementNotFoundException("User with id = " + id + " does not exist!");
    }

    public long createUser(UserRegisterDto userDto) {
        if (userDto.getPassword().equals(userDto.getConfirmationPassword())) {
            if (userRepository.findByEmail(userDto.getEmail()) != null) {
                throw new InvalidOperationException("User already exists!");
            }
            LocalDateTime now = LocalDateTime.now();
            User user = new User();
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            String pass = encoder.encode(userDto.getPassword());
            user.setPassword(pass);
            user.setDateCreated(now);
            user.setLastLogin(LocalDateTime.now());
            long id = userRepository.save(user).getId();
            userDto.setId(id);
            return id;
        }
        throw new AuthorizationException("The password and its confirmation do not match. Please try again");
    }

    public UserDto updateUser(long id, EditUserDto userDto) {
        User user = getUserById(id);
        String firstName = userDto.getFirstName();
        String lastName = userDto.getLastName();
        if(firstName != null && !firstName.isEmpty()){
            user.setFirstName(firstName);
        }
        if(lastName != null && !lastName.isEmpty()){
            user.setLastName(userDto.getLastName());
        }
        userRepository.save(user);
        return user.toDto();
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public long addAccount(long id, AccountDto accountDto) {
        return accountService.createAccount(getUserById(id), accountDto);
    }

    public UserDto logUser(LoginUserDto userDto) {
        User user = userRepository.findByEmail(userDto.getEmail());
        if (user == null) {
            throw new AuthorizationException("User could NOT be found. Please check your credentials");
        }
        boolean passwordsMatches = BCrypt.checkpw(userDto.getPassword(), user.getPassword());
        if (passwordsMatches) {
            user.setLastLogin(LocalDateTime.now());
            return this.userRepository.save(user).toDto();
        }
        throw new AuthorizationException("User could NOT be found. Please check your credentials");
    }

    public List<User> getInactiveUsers(LocalDate date) {
        return this.userRepository.findAllByLastLoginBefore(date);
    }

    public UserDto changePassword(long userId, UserChangePasswordDto userDto) {
        User user = getUserById(userId);
        if (!BCrypt.checkpw(userDto.getOldPassword(), user.getPassword())) {
            throw new InvalidOperationException("Changing password can not be proceed!");
        }
        if (!userDto.getNewPassword().equals(userDto.getConfirmNewPassword())) {
            throw new InvalidOperationException("The new password and its confirmation do not match.");
        }
        String password = encoder.encode(userDto.getNewPassword());
        user.setPassword(password);
        return userRepository.save(user).toDto();
    }
}
