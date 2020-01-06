package ittalents.javaee.model;

import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailRemainder {

    private final String subject = "";
    private final String body = "";

    @Autowired
    private UserService userService;

    @Scheduled
    public void sendEmailToRemain(){

    }
}
