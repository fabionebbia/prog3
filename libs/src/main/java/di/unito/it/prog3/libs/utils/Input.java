package di.unito.it.prog3.libs.utils;

import javafx.beans.value.ObservableValue;

public class Input {

    public static boolean isBlank(String input) {
        return input == null || input.isBlank();
    }

    public static boolean isBlank(ObservableValue<String> input) {
        return isBlank(input.getValue());
    }

}
