package ittalents.javaee.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionManager {

    public static final String LOGGED = "logged";
    public static final String EXPIRED_SESSION = "Your session has expired! Please, log in again!";

    public static boolean validateLogged(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (session.isNew()) {
            return false;
        }

        if (session.getAttribute(LOGGED) == null || session.getAttribute(LOGGED).equals(false)) {
            return false;
        } else {
            return true;
        }
    }

    public static void logUser(HttpServletRequest req) {
        // session.setAttribute(req, user ? -> serializable User ?)
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(60 * 10); // 10 minutes
        session.setAttribute(LOGGED, true);
    }
}
