package di.unito.it.prog3.libs.utils;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public final class Emails {

    private Emails() {}

    public static final DateTimeFormatter VISUAL_TIMESTAMP_DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm");

    private static final Pattern emailRegex = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+))");

    public static boolean isWellFormed(String email) {
        if (email == null) return false;
        return emailRegex.matcher(email.toLowerCase()).matches();
    }

    public static boolean isMalformed(String email) {
        return !isWellFormed(email);
    }

    public static boolean isIdWellFormed(Email.ID id) {
        try {
            Email.ID.fromString(id.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
