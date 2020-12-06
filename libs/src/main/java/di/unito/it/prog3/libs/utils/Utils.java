package di.unito.it.prog3.libs.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

    public static String toUpperFirst(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    public static String toTitleCase(String str, String separator) {
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


    public static String toCamelCase(Enum<?> e) {
        StringBuilder sb = new StringBuilder();

        String[] tokens = e.name().toLowerCase().split("_+");
        sb.append(tokens[0]);

        for (int i = 1; i < tokens.length; i++) {
            String capitalizedToken = tokens[i]
                    .substring(0, 1)
                    .toUpperCase()
                    .concat(tokens[i].substring(1));
            sb.append(capitalizedToken);
        }

        return sb.toString();
    }

}
