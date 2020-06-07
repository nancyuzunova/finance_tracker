package ittalents.javaee.model.mail;

import ittalents.javaee.model.pojo.User;
import ittalents.javaee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@EnableScheduling
public class MailReminder {

    private static final int DAYS_TO_SUBTRACT = 10;

    private final String subject = "It's been awhile";
    private final String body = "Do you still want to track your finance?" + System.lineSeparator()
            + "You have used FinanceTrackerNL for some time, but then you just disappeared. " +
            "You can come back and continue tracking your financial now in awesome way!";

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0 * * * *")
    public void sendEmailToRemain() {
        for (User user : userService.getInactiveUsers(LocalDateTime.now().minusDays(DAYS_TO_SUBTRACT))) {
            Thread thread = new Thread(){
                @Override
                public void run() {
                    MailSender.sendMail(user.getEmail(), subject, body);
                }
            };
        }
    }
}
