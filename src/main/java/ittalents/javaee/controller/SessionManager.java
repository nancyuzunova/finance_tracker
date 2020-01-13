package ittalents.javaee.controller;

import ittalents.javaee.model.dto.UserDto;
import ittalents.javaee.model.dto.UserRegisterDto;

import javax.servlet.http.HttpSession;

public class SessionManager {

    public static final String LOGGED = "logged";
    private static final int SESSION_EXPIRATION_TIME = 60 * 30; // 30 minutes

    public static boolean validateLogged(HttpSession session) {
        if (session.isNew() || session.getAttribute(LOGGED) == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void logUser(HttpSession session, UserDto user) {
        session.setMaxInactiveInterval(SESSION_EXPIRATION_TIME);
        session.setAttribute(LOGGED, user);
    }

    public static void registerAndLogUser(HttpSession session, UserRegisterDto user) {
        UserDto userDto = new UserDto(user);
        logUser(session, userDto);
    }
}
