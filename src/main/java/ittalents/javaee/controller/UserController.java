package ittalents.javaee.controller;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.LoginUserDto;
import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.dto.UserRegisterDto;
import ittalents.javaee.service.AccountService;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
public class UserController extends AbstractController{

    private UserService userService;
    private AccountService accountService;

    @Autowired
    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @GetMapping("/users")
    public ResponseEntity getUsers() {
        List<UserDto> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUserById(@PathVariable @Positive long id) {
        UserDto user = userService.getUserById(id).toDto();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}/accounts")
    public ResponseEntity getAccountsByUserId(@PathVariable @Positive long id) {
        List<AccountDto> accounts = this.accountService.getAllAccountsByUserId(id);
        return ResponseEntity.ok(accounts);
    }

//    @GetMapping("/users/{id}/transfers")
//    public List<TransferDto> getAllTransfersByUserId(@PathVariable long id){
//
//    }

    @PostMapping("/users/register")
    public ResponseEntity register(@RequestBody UserRegisterDto user) {
        URI location = URI.create(String.format("/users/%d", userService.createUser(user)));
        //TODO log user using SessionManager
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/users/login")
    public ResponseEntity login(@RequestBody @Valid LoginUserDto loginUserDto){
        UserDto dto = this.userService.logUser(loginUserDto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/users/logout")
    public ResponseEntity logout(HttpSession session){
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/accounts")
    public ResponseEntity addAccount(@PathVariable @Positive long id, @RequestBody @Valid AccountDto accountDto) {
        URI location = URI.create(String.format("/accounts/%d", this.userService.addAccount(id, accountDto)));
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity updateUser(@PathVariable @Positive long id, @RequestBody @Valid UserDto userDto, HttpServletRequest req) {
        // TODO see when to log user
        if (SessionManager.validateLogged(req)) {
            UserDto user = userService.updateUser(id, userDto).toDto();
            return ResponseEntity.ok(user);
        } else {
            System.out.println(SessionManager.EXPIRED_SESSION);
            return ResponseEntity.status(HttpStatus.valueOf(440)).build(); // 440 Login Time-out
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable @Positive long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
