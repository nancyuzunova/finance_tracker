package ittalents.javaee;

import ittalents.javaee.controller.SessionManager;
import ittalents.javaee.exceptions.AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoggedInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (!SessionManager.validateLogged(session)) {
            throw new AuthorizationException("You must log in!");
        }
        return true;
    }
}
