package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.AccountDto;
import ittalents.javaee.model.BudgetDto;
import ittalents.javaee.model.User;
import ittalents.javaee.model.UserDto;
import ittalents.javaee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private AccountService accountService;
    private BudgetService budgetService;

    @Autowired
    public UserService(UserRepository userRepository, AccountService accountService, BudgetService budgetService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.budgetService = budgetService;
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

    public long createUser(UserDto userDto) {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.fromDto(userDto);
        user.setDateCreated(now);
        // TODO - see how to update last login time
        user.setLastLogin(now);
        return userRepository.save(user).getId();
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

    public long addBudget(long id, BudgetDto budgetDto) {
        return budgetService.createBudget(getUserById(id), budgetDto);
    }
}
