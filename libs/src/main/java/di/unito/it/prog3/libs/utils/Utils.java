package di.unito.it.prog3.libs.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

    public static final String toTitleCase(String str, String separator) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Arrays
                .stream(str.split(separator))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toTitleCase(word.charAt(0))
                                + word.substring(1).toLowerCase())
                .collect(Collectors.joining(separator));
    }

    public static final String toTitleCase(String str) {
        return toTitleCase(str, " ");
    }

    public static <E extends Enum<E>> String toTitleCase(E e) {
        return toTitleCase(e.name(), "_").replaceAll(" +", " ");
    }

}
