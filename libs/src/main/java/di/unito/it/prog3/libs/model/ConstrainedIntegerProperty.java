package di.unito.it.prog3.libs.model;

import javafx.beans.property.*;

import java.util.function.Predicate;

public class ConstrainedIntegerProperty extends SimpleIntegerProperty implements ConstrainedProperty {

    private final ReadOnlyBooleanWrapper valid;

    public ConstrainedIntegerProperty(Predicate<Integer> constraint) {
        valid = new ReadOnlyBooleanWrapper(true);

        addListener((observable, oldValue, newValue) -> {
            int value = newValue.intValue();
            valid.set(constraint.test(value));
        });
    }

    public ConstrainedIntegerProperty(Predicate<Integer> constraint, int initialValue) {
        this(constraint);
        set(initialValue);
    }

    @Override
    public final ReadOnlyBooleanProperty isValid() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public void addValidationListener(ValidationListener listener) {
        valid.addListener(listener);
    }

    @Override
    public void removeValidationListener(ValidationListener listener) {
        valid.removeListener(listener);
    }

}
