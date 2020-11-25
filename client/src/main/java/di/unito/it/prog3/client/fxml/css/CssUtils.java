package di.unito.it.prog3.client.fxml.css;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Styleable;

// TODO needs refactoring
public class CssUtils {

    public static void toggle(Styleable styleable, String styleClass) {
        ObservableList<String> classList = styleable.getStyleClass();
        if (classList.contains(styleClass)) {
            classList.remove(styleClass);
        } else {
            classList.add(styleClass);
        }
    }

    public static boolean ensureClassSet(Styleable styleable, String className) {
        ObservableList<String> classList = styleable.getStyleClass();
        if (!classList.contains(className)) {
            classList.add(className);
        }
        return true;
    }

    public static boolean ensureClassUnset(Styleable styleable, String className) {
        ObservableList<String> classList = styleable.getStyleClass();
        classList.remove(className);
        return false;
    }

    public static void conditionallySetClass(Styleable styleable, String className, boolean condition) {
        if (condition) {
            ensureClassSet(styleable, className);
        } else {
            ensureClassUnset(styleable, className);
        }
    }

    public static void ensureOnlyOfGroup(Styleable styleable, String styleClass) {
        ObservableList<String> classList = styleable.getStyleClass();
        String[] parts = styleClass.split("--");

        if (parts.length == 2) {
            classList.removeIf(alreadySet -> alreadySet.startsWith(parts[0]));
        }

        classList.add(styleClass);
    }

    public static CssBinder style(Styleable styleable) {
        return new CssBinder(styleable);
    }

    public static class CssBinder {

        private final Styleable styleable;

        private CssBinder(Styleable styleable) {
            this.styleable = styleable;
        }

        public void booleanToggle(String targetClass, ObservableValue<Boolean> observableBoolean) {
            observableBoolean.addListener((observable, wasTrue, isTrue) -> {
                ObservableList<String> classList = styleable.getStyleClass();
                if (isTrue && !classList.contains(targetClass)) {
                    classList.add(targetClass);
                } else if (!isTrue) {
                    classList.remove(targetClass);
                }
            });
        }
    }

}
