package ittalents.javaee.controller;

import ittalents.javaee.model.dto.*;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;

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
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/register")
    public ResponseEntity register(HttpSession session, @RequestBody @Valid UserRegisterDto user) {
        URI location = URI.create(String.format("/users/%d", userService.createUser(user)));
        SessionManager.registerAndLogUser(session, user);
        return ResponseEntity.created(location).build();
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
        userService.deleteUser(((UserDto) session.getAttribute(SessionManager.LOGGED)).getId());
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
