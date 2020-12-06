package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.forms.FormException.InvalidValueException;
import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.util.function.Predicate;

public final class FormField<V> {

    private Label errorLabel;
    private final TextInputControl control;

    private final StringProperty errorMessage;
    private final BooleanProperty userInteracted;
    private final ObjectProperty<V> validatedValue;

    private final UserInteractionListener userInteractionListener;

    private final Enum<?> enumKey;

    protected FormField(Enum<?> enumKey, TextInputControl control, Validator<V> validator) {
        this.enumKey = enumKey;
        this.control = control;

        errorMessage = new SimpleStringProperty();
        userInteracted = new SimpleBooleanProperty();
        validatedValue = new SimpleObjectProperty<>();
        userInteractionListener = new UserInteractionListener(validator);

        control.focusedProperty().addListener(userInteractionListener);
    }

    public FormField<V> attachErrorLabel(Label errorLabel) {
        this.errorLabel = errorLabel;
        errorLabel.textProperty().bind(errorMessage);
        CssUtils.ensureClassSet(errorLabel, "error-label");
        CssUtils.ensureClassSetOnlyIf(errorLabel, "error-occurred", isInvalid());
        return this;
    }

    public FormField<V> initialize(String text) {
        if (text != null) {
            if (userInteracted.get()) {
                throw new FormException(enumKey, "already initialized");
            }
            control.setText(text);
        }
        return this;
    }

    public void setErrorMessage(String message) {
        errorMessage.set(message);
    }

    public BooleanBinding isValid() {
        return userInteracted.not().or(validatedValue.isNotNull());
    }

    public BooleanBinding isInvalid() {
        return isValid().not();
    }


    protected TextInputControl getControl() {
        return control;
    }

    protected V get() {
        commit();
        if (isInvalid().get()) {
            throw new IllegalStateException("Field value is invalid");
        }
        return validatedValue.get();
    }

    protected boolean needsFocus() {
        return userInteracted.not().or(isInvalid()).get();
    }

    protected boolean commit() {
        userInteracted.set(true);
        userInteractionListener.validate();
        return isValid().get();
    }

    class UserInteractionListener implements ChangeListener<Boolean> {
        private final Validator<V> validator;
        private String previousInput;

        UserInteractionListener(Validator<V> validator) {
            this.validator = validator;
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
                V newValidatedInput;
                try {
                    newValidatedInput = validator.validate(newInput);
                } catch (InvalidValueException e) {
                    String validationError = e.computeErrorMessage(enumKey);
                    setErrorMessage(validationError);
                    newValidatedInput = null;
                }

                validatedValue.set(newValidatedInput);
                previousInput = newInput;
                userInteracted.set(true);
            }
        }

        private void validate() {
            validateIf(noMatterWhat -> true);
        }
    }
}
