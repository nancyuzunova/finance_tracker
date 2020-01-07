package ittalents.javaee.service;

import ittalents.javaee.exceptions.AuthorizationException;
import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.LoginUserDto;
import ittalents.javaee.model.dto.UserRegisterDto;
import ittalents.javaee.model.pojo.User;
import ittalents.javaee.model.dto.UserDto;
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

    public List<UserDto> getUsers() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(user.toDto());
        }
        return users;
    }

    public User getUserById(long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get();
        }

        throw new ElementNotFoundException("User with id = " + id + " does not exist!");
    }

    public long createUser(UserRegisterDto userDto) {
        if(userDto.getPassword().equals(userDto.getConfirmationPassword())) {
            if(userRepository.findByEmail(userDto.getEmail()) != null){
                throw new InvalidOperationException("Username already exists!");
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
            return userRepository.save(user).getId();
        }
        throw new AuthorizationException("The password and its confirmation do not match. Please try again");
    }

    public User updateUser(long id, UserDto userDto) {
        User user = getUserById(id);
        user.fromDto(userDto);
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public long addAccount(long id, AccountDto accountDto) {
        return accountService.createAccount(getUserById(id), accountDto);
    }

    public UserDto logUser(LoginUserDto userDto) {
        boolean passwordsMatches = BCrypt.checkpw(userDto.getPassword(), userRepository.findByEmail(userDto.getEmail()).getPassword());
        if(passwordsMatches){
            User user = userRepository.findByEmail(userDto.getEmail());
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                return this.userRepository.save(user).toDto();
            }
        }
        throw new AuthorizationException("User could NOT be found. Please check your credentials");
    }

    public List<User> getInactiveUsers(LocalDate date){
        return this.userRepository.findAllByLastLoginBefore(date);
    }
}
