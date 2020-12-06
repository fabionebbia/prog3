package di.unito.it.prog3.libs.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;

import java.util.function.Predicate;

public class ConstrainedStringProperty extends SimpleStringProperty implements ConstrainedProperty {

    private final ReadOnlyBooleanWrapper valid;

    public ConstrainedStringProperty(Predicate<String> constraint) {
        valid = new ReadOnlyBooleanWrapper();

        addListener((observable, oldValue, newValue) ->
                valid.set(constraint.test(newValue))
        );
    }

    public ConstrainedStringProperty(Predicate<String> constraint, String initialValue) {
        this(constraint);
        set(initialValue);
    }

    @Override
    public ReadOnlyBooleanProperty isValid() {
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
