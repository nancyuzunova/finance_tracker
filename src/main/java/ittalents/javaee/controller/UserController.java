package ittalents.javaee.controller;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.LoginUserDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.dto.UserRegisterDto;
import ittalents.javaee.service.AccountService;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@Validated
public class UserController extends AbstractController {

    private UserService userService;
    private AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/users/all")
    public ResponseEntity getUsers() {
        List<UserDto> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/profile")
    public ResponseEntity getUserById(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/accounts")
    public ResponseEntity getAccountsByUserId(HttpSession session) {
        List<AccountDto> accounts = this.accountService.
                getAllAccountsByUserId(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId());
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/users/register")
    public ResponseEntity register(HttpSession session, @RequestBody @Valid UserRegisterDto user) {
        URI location = URI.create(String.format("/users/%d", userService.createUser(user)));
        SessionManager.registerAndLogUser(session, user);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/users/login")
    public ResponseEntity login(HttpSession session, @RequestBody @Valid LoginUserDto loginUserDto){
        UserDto dto = this.userService.logUser(loginUserDto);
        SessionManager.logUser(session, dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/users/logout")
    public ResponseEntity logout(HttpSession session){
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/accounts")
    public ResponseEntity addAccount(HttpSession session, @RequestBody @Valid AccountDto accountDto) {
        URI location = URI.create(String.format("/accounts/%d",
                this.userService
                        .addAccount(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId(), accountDto)));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/users/edit")
    public ResponseEntity updateUser(HttpSession session, @RequestBody @Valid UserDto userDto) {
        UserDto user = userService
                .updateUser(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId(), userDto).toDto();
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity deleteUser(HttpSession session) {
        userService.deleteUser(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId());
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
