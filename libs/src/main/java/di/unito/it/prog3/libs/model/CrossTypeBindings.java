package di.unito.it.prog3.libs.model;

import javafx.beans.property.Property;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class CrossTypeBindings {

    private CrossTypeBindings() {}

    public static void bindStringToInteger(Property<Integer> integer, Property<String> string) {
        string.addListener((observable, oldValue, newValue) -> {
            String strValue = string.getValue();
            int intValue = Integer.parseInt(strValue);
            integer.setValue(intValue);
        });
    }

    public static void bindIntegerToString(Property<String> string, Property<Integer> integer) {
        string.addListener((observable, oldValue, newValue) -> {
            int intValue = integer.getValue();
            String strValue = String.valueOf(intValue);
            string.setValue(strValue);
        });
    }

    public static void bindBidirectional(Property<String> string, Property<Number> integer) {
        final AtomicBoolean stringChanged = new AtomicBoolean(false);
        final AtomicBoolean integerChanged = new AtomicBoolean(false);

        string.addListener((observable, oldValue, newValue) -> {
            stringChanged.set(true);

            if (!integerChanged.get()) {
                int intValue = Integer.parseInt(newValue);
                integer.setValue(intValue);
            }

            integerChanged.set(false);
        });

        integer.addListener((observable, oldValue, newValue) -> {
            integerChanged.set(true);

            if (!stringChanged.get()) {
                String strValue = String.valueOf(newValue);
                string.setValue(strValue);
            }

            stringChanged.set(false);
        });
    }

}
