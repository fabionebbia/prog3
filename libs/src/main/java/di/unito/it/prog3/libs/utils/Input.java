package di.unito.it.prog3.libs.utils;

import java.util.function.Predicate;

public class Input {

    public static boolean isBlank(String input) {
        return input == null || input.isBlank();
    }

    public static boolean isNotBlank(String input) {
        return !isBlank(input);
    }

    public static boolean isPort(String input) {
        if (isNotBlank(input)) {
            try {
                int port = Integer.parseInt(input);
                return 1 <= port && port <= 65535;
            } catch (NumberFormatException ignored) {}
        }
        return false;
    }

    public static boolean isEmail(String input) {
        return isNotBlank(input) && Emails.isWellFormed(input);
    }

    public static <T> void ensure(T input, Predicate<T> predicate, String exceptionMessage) {
        if (!predicate.test(input)) {
            throw new IllegalArgumentException(exceptionMessage); // TODO
        }
    }

}
