package ittalents.javaee.controller;

import ittalents.javaee.model.dto.UserDto;

import javax.servlet.http.HttpSession;

public class SessionManager {

    public static final String LOGGED = "logged";
    private static final int SESSION_EXPIRATION_TIME = 60 * 10; // 10 minutes

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
}
