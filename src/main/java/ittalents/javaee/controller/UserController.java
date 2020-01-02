package ittalents.javaee.controller;

import ittalents.javaee.model.*;
import ittalents.javaee.service.AccountService;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
public class UserController {

    private UserService userService;
    private AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable @PositiveOrZero long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/accounts")
    public List<AccountDto> getAccountsByUserId(@PathVariable long id) {
        return this.accountService.getAllAccountsByUserId(id);
    }

//    @GetMapping("/users/{id}/transfers")
//    public List<TransferDto> getAllTransfersByUserId(@PathVariable long id){
//
//    }

    @PostMapping("/users")
    public void createUser(@RequestBody @Valid UserDto user) {
        userService.createUser(user);
    }

    @PostMapping("/users/{id}/accounts")
    public void addAccount(@PathVariable @PositiveOrZero long id, @RequestBody @Valid AccountDto accountDto) {
        this.userService.addAccount(id, accountDto);
    }

    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable @PositiveOrZero long id, @RequestBody @Valid UserDto userDto, HttpServletRequest req) {
        // TODO see when to log user
        if (SessionManager.validateLogged(req)) {
            userService.updateUser(id, userDto);
        } else {
            System.out.println(SessionManager.EXPIRED_SESSION);
        }
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
