package ittalents.javaee.model;

import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MailRemainder {

    private static final int MAX_INACTIVE_DAYS = 10 * 24 * 60 * 60 * 1000; // ms

    private final String subject = "It's been awhile";
    private final String body = "Do you still want to track your finance?" + System.lineSeparator()
            + "You have used FinanceTrackerNL for some time, but then you just disappeared. " +
            "You can come back and continue tracking your financial now in awesome way!";

    @Autowired
    private UserService userService;

    @Scheduled(fixedRate = MAX_INACTIVE_DAYS)
    public void sendEmailToRemain() {
        for (User user : userService.getInactiveUsers(LocalDate.now().minusDays(10))) {
            MailSender.sendMail(user.getEmail(), subject, body);
        }
    }
}
