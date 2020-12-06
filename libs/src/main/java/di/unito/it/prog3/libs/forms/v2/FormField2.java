package di.unito.it.prog3.libs.forms.v2;

import di.unito.it.prog3.libs.forms.Form;
import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private final Field classField;
    private final BooleanProperty isValid;
    private final StringProperty errorMessage;
    private final List<Constraint> constraints;
    private final BooleanProperty committed;
    private UserInteractionListener userInteractionListener;
    private String initialText;

    private Label errorLabel;
    private TextInputControl control;

    protected FormField2(Field classField, Constraint[] constraints) {
        this.classField = classField;
        this.constraints = new ArrayList<>();
        this.constraints.addAll(Arrays.asList(constraints));

        isValid = new SimpleBooleanProperty();
        errorMessage = new SimpleStringProperty();
        committed = new SimpleBooleanProperty();
    }

    protected String getKey() {
        return classField.getName();
    }

    protected abstract Object getRefined(String input);

    protected void bindControl(TextInputControl control) {
        this.control = control;
        if (initialText != null) {
            control.setText(initialText);
        }
        userInteractionListener = new UserInteractionListener();
        control.focusedProperty().addListener(userInteractionListener);
    }

    public FormField2 attachErrorLabel(Label errorLabel) {
        this.errorLabel = errorLabel;
        errorLabel.textProperty().bind(errorMessage);
        CssUtils.ensureClassSet(errorLabel, "error-label");
        CssUtils.ensureClassSetOnlyIf(errorLabel, "error-occurred", isInvalid());
        return this;
    }

    public FormField2 initialize(String text) {
        if (text != null) {
            if (committed.get()) {
                // TODO throw new FormException(enumKey, "already initialized");
            }
            if (control != null) {
                control.setText(text);
            } else {
                initialText = text;
            }
        }
        return this;
    }

    public void setErrorMessage(String message) {
        isValid.set(false);
        errorMessage.set(message);
    }

    public BooleanBinding isValid() {
        return committed.not().or(isValid);
    }

    public BooleanBinding isInvalid() {
        return isValid().not();
    }

    protected TextInputControl getControl() {
        return control;
    }

    protected boolean needsFocus() {
        return committed.not().or(isInvalid()).get();
    }

    protected boolean commit(Form2 form) {
        committed.set(true);
        userInteractionListener.validate();
        boolean validated = isValid.get();

        if (validated) {
            try {
                classField.setAccessible(true);
                classField.set(form, getRefined(control.getText()));
                classField.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // TODO
            }
        }

        return validated;
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
            if (isInvalid().get()) {
                control.setText("");
            }
        }

        private void onFocusLost() {
            validateIf(newInput -> newInput != null && !newInput.equals(previousInput));
        }

        private void validateIf(Predicate<String> condition) {
            String newInput = control.getText();

            if (condition.test(newInput)) {
                boolean result = constraints.isEmpty()
                        || constraints.stream()
                        .map(constraint -> constraint.test(newInput))
                        .reduce(true, (prev, curr) -> prev && curr);

                if (!result) {
                    String validationError = "some error"; // TODO e.computeErrorMessage(enumKey);
                    setErrorMessage(validationError);
                }

                isValid.set(result);
                committed.set(true);
                previousInput = newInput;
            }
        }

        private void validate() {
            validateIf(noMatterWhat -> true);
        }
    }

}
