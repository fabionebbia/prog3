package di.unito.it.prog3.client.fxml.css;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Style;
import javafx.css.Styleable;

// TODO needs refactoring
public class CssUtils {

    public static boolean hasClass(Styleable styleable, String cssClass) {
        return styleable.getStyleClass().contains(cssClass);
    }

    public static void toggle(Styleable styleable, String cssClass) {
        ensureClassSetOnlyIf(styleable, cssClass, !hasClass(styleable, cssClass));

        /*if (styleable.getStyleClass().contains(cssClass)) {
            ensureClassUnset(styleable, cssClass);
        } else {
            ensureClassSet(styleable, cssClass);
        }*/
    }

    public static void ensureClassSet(Styleable styleable, String cssClass) {
        ObservableList<String> classList = styleable.getStyleClass();
        if (!hasClass(styleable, cssClass)) {
            classList.add(cssClass);
        }
    }

    public static void ensureClassUnset(Styleable styleable, String cssClass) {
        styleable.getStyleClass().removeIf(c -> c.equals(cssClass));
    }

    public static void ensureClassSetOnlyIf(Styleable styleable, String cssClas, boolean condition) {
        if (condition) {
            ensureClassSet(styleable, cssClas);
        } else {
            ensureClassUnset(styleable, cssClas);
        }
    }

    public static void ensureClassSetOnlyIf(Styleable styleable, String cssClass, ObservableValue<Boolean> observableCondition) {
        ensureClassSetOnlyIf(styleable, cssClass, observableCondition.getValue());

        observableCondition.addListener((observable, wasConditionTrue, isConditionTrue) ->
            ensureClassSetOnlyIf(styleable, cssClass, isConditionTrue)
        );
    }

    public static void ensureOnlyClassOfGroup(Styleable styleable, String cssClass) {
        ObservableList<String> classList = styleable.getStyleClass();
        String[] parts = cssClass.split("--");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Not a group");
        }

        classList.removeIf(c -> c.startsWith(parts[0]));
        ensureClassSet(styleable, cssClass);
    }

}
