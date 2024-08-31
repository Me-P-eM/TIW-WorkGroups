package it.polimi.progettotiw.purehtml.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This is a util class. It checks if an email is valid.
 */
public class EmailValidator {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * This method checks if an email is valid
     * @param email the email
     * @return false if it's not valid, true if it's valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
