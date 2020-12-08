package di.unito.it.prog3.libs.utils;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.Style;
import javafx.css.Styleable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;


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
        //styleable.getStyleClass().removeIf(c -> c.equals(cssClass));
        styleable.getStyleClass().removeAll(cssClasses);
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

    public static void booleanBindClass(ObservableValue<Boolean> observableCondition,
                                        Styleable styleable,
                                        String cssClass) {
        ensureClassSetOnlyIf(styleable, cssClass, observableCondition);
    }

    public static void ensureClassSetOnlyIf(Styleable styleable,
                                            String cssClass,
                                            Callable<Boolean> fun,
                                            Observable... dependencies) {
        BooleanBinding condition = Bindings.createBooleanBinding(fun, dependencies);
        ensureClassSetOnlyIf(styleable, cssClass, condition);
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

    public static class Styler {
        private final Styleable styleable;
        private final ObservableList<String> classList;

        private Styler(Styleable styleable) {
            this.styleable = styleable;
            this.classList = styleable.getStyleClass();
        }

        public static Styler style(Styleable styleable) {
            return new Styler(styleable);
        }

        public <T> void bindClassGroupExclusively(ObservableValue<T> value, Function<T, String> mapper) {
            value.addListener((observable, oldValue, newValue) ->
                    ensureClassSetGroupExclusive(styleable, mapper.apply(newValue))
            );
        }

        public void bindClass(String cssClass, ObservableValue<Boolean> observableCondition) {
            observableCondition.addListener((observable, wasConditionTrue, isConditionTrue) ->
                    CssUtils.ensureClassSetOnlyIf(styleable, cssClass, isConditionTrue)
            );
        }

        public <T> void bindClass(String cssClass, ObservableValue<T> observableValue, T targetValue) {
            observableValue.addListener((observable, oldValue, newValue) ->
                    CssUtils.ensureClassSetOnlyIf(styleable, cssClass, newValue == targetValue)
            );
        }

        public <T extends Enum<T>> void bindWithModifier(String cssBaseClass, ObservableValue<T> observableValue) {
            observableValue.addListener((observable, oldValue, newValue) -> {
                String cssModifier = newValue.name().toLowerCase().replace('_', '-');
                String cssModifierClass = cssBaseClass + "--" + cssModifier;

                //ensureClassSet(styleable, cssBaseClass);
                ensureClassSetGroupExclusive(styleable, cssModifierClass);

                ensureClassSet(styleable, cssBaseClass, cssModifierClass);
            });
        }

    }

}
