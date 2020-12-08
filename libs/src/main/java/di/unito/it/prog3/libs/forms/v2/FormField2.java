package di.unito.it.prog3.libs.forms.v2;

import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class FormField2 {

    private final ReadOnlyBooleanWrapper hasError;
    private final ReadOnlyBooleanWrapper userInteracted;

    private final Field classField;
    private final StringProperty errorMessage;
    private final List<Constraint> constraints;
    private UserInteractionListener userInteractionListener;

    private TextInputControl control;
    private Label errorLabel;


    protected FormField2(Field classField, Constraint[] constraints) {
        this.classField = classField;
        this.constraints = new ArrayList<>();
        this.constraints.addAll(Arrays.asList(constraints));

        errorMessage = new SimpleStringProperty();

        hasError = new ReadOnlyBooleanWrapper();
        userInteracted = new ReadOnlyBooleanWrapper();
    }

    protected abstract Object getRefined(String input);

    protected void bindControl(TextInputControl control) {
        this.control = control;
        userInteractionListener = new UserInteractionListener();
        control.focusedProperty().addListener(userInteractionListener);
    }

    public FormField2 set(String text) {
        if (control == null) {
            throw new IllegalStateException("Cannot set unregistered form field");
        }

        control.setText(text);
        userInteracted.set(true);
        userInteractionListener.validate();

        return this;
    }

    public FormField2 attachErrorLabel(Label errorLabel) {
        this.errorLabel = errorLabel;

        errorLabel.textProperty().bind(errorMessage);
        CssUtils.ensureClassSet(errorLabel, "error-label");
        CssUtils.ensureClassSetOnlyIf(errorLabel, "error-occurred", hasError);

        return this;
    }

    public void setErrorMessage(String message) {
        hasError.set(true);
        errorMessage.set(message);
    }

    public ReadOnlyBooleanProperty hasError() {
        return hasError.getReadOnlyProperty();
    }

    /*public BooleanBinding canSubmit() {
        userInteractionListener.validate();
        return hasError.not();
    }*/

    protected boolean needsFocus() {
        return hasError.get() || (!userInteracted.get() && constraints.contains(Constraints.REQUIRED));
    }

    protected String getKey() {
        return classField.getName();
    }

    protected TextInputControl getControl() {
        return control;
    }

    protected boolean commit(Form2 form) {
        boolean canCommit = !hasError.get();

        if (canCommit) {
            try {
                classField.setAccessible(true);
                classField.set(form, getRefined(control.getText()));
                classField.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // TODO
            }
        }

        return canCommit;
    }

    class UserInteractionListener implements ChangeListener<Boolean> {
        private String previousInput;

        UserInteractionListener() {
            previousInput = "";
            control.focusedProperty().addListener(this);
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean wasFocused, Boolean isFocused) {
            if (isFocused) {
                onFocusAcquired();
            } else {
                onFocusLost();
            }
        }

        private void onFocusAcquired() {
            // reset text if previous input was invalid so that the user
            // can start typing without having to clear previous input himself
            if (hasError.get()) {
                control.setText("");
            }
        }

        private void onFocusLost() {
            validateIf(newInput -> newInput != null && !newInput.equals(previousInput));
        }

        private void validateIf(Predicate<String> condition) {
            String newInput = control.getText();

            if (condition.test(newInput)) {
                boolean isValid = constraints.isEmpty()
                        || constraints.stream()
                        .map(constraint -> constraint.test(newInput))
                        .reduce(true, (prev, curr) -> prev && curr);


                if (!isValid) {
                    String validationError = "some error"; // TODO e.computeErrorMessage(enumKey);
                    setErrorMessage(validationError);
                }

                hasError.set(!isValid);
                userInteracted.set(true);
                previousInput = newInput;
            }
        }

        private void validate() {
            validateIf(noMatterWhat -> true);
        }
    }

}
