package ittalents.javaee;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Util {

    private static final String PLACEHOLDER = "%@";
    public static final String NOT_EXISTING_ACCOUNT = "Account with id = %@ does NOT exist!";
    public static final String NOT_EXISTING_BUDGET = "Budget with id = %@ does NOT exist!";
    public static final String FOREIGN_BUDGET_OPERATION = "You can %@ only your own budgets!";
    public static final String DUPLICATED_ACCOUNT = "You cannot make transfer to the same account!";
    public static final String FUTURE_OPERATION = "You cannot make future %@!";
    public static final String INVALID_DATE = "Invalid date! Please try again!";
    public static final String INVALID_DATE_RANGE = "Date range not valid! Please try again!";
    public static final String INCORRECT_DATES = "Inappropriate dates! Please enter correct dates!";
    public static final String TRANSFER_TO_OTHER_USER = "You can not make transfer to other users!";
    public static final String NOT_ENOUGH_BALANCE = "Not enough balance!";
    public static final String PAST_PLANNED_PAYMENTS = "You cannot make planned payments with past dates!";
    public static final String FAR_PLANNED_PAYMENTS = "Payment too far into the future! Please check!";
    public static final String EXCEEDING_PLANNED_PAYMENTS = "You can not make planned payment exceeding the maximum amount!";
    public static final String EMAIL_SUBJECT = "NOT finished payment";
    public static final String EMAIL_BODY = "Hello,\nYour planned payment %@ has failed because of  insufficient balance " +
            "of your account. Please deposit to your account and make payment manually!";

    public static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);
    public static final LocalDate MAX_DATE = LocalDate.of(2150, 1, 1);

    public static String replacePlaceholder(Object value, String message){
        if(message.contains(PLACEHOLDER)){
            message = message.replace(PLACEHOLDER, value.toString());
        }
        return message;
    }

    public static LocalDate getConvertedDate(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
