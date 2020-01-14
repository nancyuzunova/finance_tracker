package ittalents.javaee.controller;

import ittalents.javaee.model.dto.*;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Validated
public class UserController extends AbstractController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/profile")
    public ResponseEntity getUserById(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        UserDto logged = userService.getUserById(user.getId()).toDto();
        return ResponseEntity.ok(logged);
    }

    @PostMapping("/users/register")
    public ResponseEntity register(HttpSession session, @RequestBody @Valid UserRegisterDto user) {
        UserDto registeredUser = userService.createUser(user);
        SessionManager.registerAndLogUser(session, user);
        return new ResponseEntity(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/users/login")
    public ResponseEntity login(HttpSession session, @RequestBody @Valid LoginUserDto loginUserDto) {
        UserDto dto = this.userService.logUser(loginUserDto);
        SessionManager.logUser(session, dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/users/logout")
    public ResponseEntity logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity("You have logged out!", HttpStatus.RESET_CONTENT);
    }

    @PostMapping("/users/accounts")
    public ResponseEntity addAccount(HttpSession session, @RequestBody @Valid AccountDto accountDto) {
        AccountDto account =
                this.userService.addAccount(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId(), accountDto);
        return new ResponseEntity(account, HttpStatus.CREATED);
    }

    @PutMapping("/users/edit")
    public ResponseEntity updateUser(HttpSession session, @RequestBody @Valid EditUserDto userDto) {
        UserDto user = userService
                .updateUser(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId(), userDto);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/changePassword")
    public ResponseEntity<UserDto> changePassword(HttpSession session, @RequestBody @Valid UserChangePasswordDto dto) {
        UserDto user = userService.changePassword(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId(), dto);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity deleteUser(HttpSession session) {
        UserDto user = (UserDto) session.getAttribute(SessionManager.LOGGED);
        userService.deleteUser(user.getId());
        session.invalidate();
        return ResponseEntity.ok(user);
    }
}
