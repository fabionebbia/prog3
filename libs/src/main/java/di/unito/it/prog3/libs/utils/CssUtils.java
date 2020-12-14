package di.unito.it.prog3.libs.utils;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;


public final class CssUtils {

    private CssUtils() {}


    public static boolean hasClass(Styleable styleable, String cssClass) {
        return styleable.getStyleClass().contains(cssClass);
    }


    public static void toggleModifier(Styleable styleable, String cssBaseClass, String cssModifierClass) {
        String cssClass = cssBaseClass + "--" + cssModifierClass;
        ensureClassSetOnlyIf(styleable, cssClass, !hasClass(styleable, cssClass));
        ensureClassSet(styleable, cssBaseClass);
    }


    public static void ensureClassSet(Styleable styleable, String... cssClasses) {
        ObservableList<String> classList = styleable.getStyleClass();
        for (String cssClass : cssClasses) {
            if (!hasClass(styleable, cssClass)) {
                classList.add(cssClass);
            }
        }
    }


    public static void ensureClassUnset(Styleable styleable, String... cssClasses) {
        styleable.getStyleClass().removeAll(cssClasses);
    }


    public static void ensureClassSetOnlyIf(Styleable styleable, String cssClass, boolean condition) {
        if (condition) {
            ensureClassSet(styleable, cssClass);
        } else {
            ensureClassUnset(styleable, cssClass);
        }
    }


    public static void ensureClassSetOnlyIf(Styleable styleable,
                                            String cssClass,
                                            ObservableValue<Boolean> observableCondition) {
        ensureClassSetOnlyIf(styleable, cssClass, observableCondition.getValue());

        observableCondition.addListener((observable, wasConditionTrue, isConditionTrue) ->
            ensureClassSetOnlyIf(styleable, cssClass, isConditionTrue)
        );
    }


    public static void ensureClassSetGroupExclusive(Styleable styleable, String cssClass) {
        ObservableList<String> classList = styleable.getStyleClass();
        String[] parts = cssClass.split("--");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Not a group");
        }

        classList.removeIf(c -> c.startsWith(parts[0]));
        ensureClassSet(styleable, cssClass);
    }

}
