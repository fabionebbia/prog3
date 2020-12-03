package di.unito.it.prog3.libs.forms;

import com.fasterxml.jackson.annotation.JsonValue;
import di.unito.it.prog3.libs.forms.FormException.InvalidValueException;
import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

public final class Field<V> {

    private final BooleanProperty isInitialized;
    private final TextInputControl control;
    private final Validator<V> validator;
    private final Enum<?> enumKey;

    private final ObjectProperty<V> validatedValue;

    private Label errorLabel;

    Field(Enum<?> enumKey, TextInputControl control, Validator<V> validator, String initText) {
        this.enumKey = enumKey;
        this.control = control;
        this.validator = validator;
        this.isInitialized = new SimpleBooleanProperty();
        this.validatedValue = new SimpleObjectProperty<>();

        control.textProperty().addListener(new InitializationListener());

        initialize(initText);
    }

    Enum<?> getKey() {
        return enumKey;
    }

    TextInputControl getControl() {
        return control;
    }

    public Field<V> setErrorLabel(Label errorLabel) {
        this.errorLabel = errorLabel;
        CssUtils.ensureClassSet(errorLabel, "error-label");
        CssUtils.ensureClassSetOnlyIf(errorLabel, "error-occurred", isInvalid());
        return this;
    }

    public Field<V> initialize(String text) {
        if (text != null) {
            if (!isInitialized.get()) {
                control.setText(text);
            } else {
                throw new FormException(enumKey, "already initialized");
            }
        }
        return this;
    }

    private BooleanBinding valid;
    public BooleanBinding isValid() {
        if (valid == null) valid = isInitialized.not().or(validatedValue.isNotNull());
        return valid;
    }

    private BooleanBinding invalid;
    public BooleanBinding isInvalid() {
        if (invalid == null) invalid = isValid().not();
        return invalid;
    }

    protected boolean needsFocus() {
        System.out.println(isInitialized.not().get() + " " + isInvalid().get());
        return isInitialized.not().and(isInvalid()).get();
    }

    @JsonValue
    private V getJsonSerialization() {
        return validatedValue.get();
    }

    protected String getJsonKey() {
        StringBuilder sb = new StringBuilder();

        String[] tokens = enumKey.name().split("_+");
        sb.append(tokens[0]);

        for(int i = 1; i < tokens.length; i++) {
            String capitalizedToken = tokens[i]
                    .substring(0, 1)
                    .toUpperCase()
                    .concat(tokens[i].substring(1));
            sb.append(capitalizedToken);
        }

        return sb.toString();
    }


    class InitializationListener implements ChangeListener<String> {

        InitializationListener() {
            isInitialized.set(false);
        }

        public void changed(ObservableValue<? extends String> observable, String oldInput, String newInput) {
            control.textProperty().removeListener(this);
            control.focusedProperty().addListener(new FieldChangeListener());
        }
    }


    class FieldChangeListener implements ChangeListener<Boolean> {

        private String previousInput;

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
                control.setText(null);
                control.focusedProperty().removeListener(this);
                control.textProperty().addListener(new InitializationListener());
            }
        }

        private void onFocusLost() {
            String newInput = control.getText();
            boolean hasInputChanged = previousInput == null || !previousInput.equals(newInput);

            if (hasInputChanged) {
                V newValidatedInput;
                try {
                    newValidatedInput = validator.validate(newInput);
                    System.out.println("New validated input: " + newValidatedInput);
                } catch (InvalidValueException e) {
                    newValidatedInput = null;
                    // TODO
                    // System.out.println(new InvalidValueException(enumKey, e.getMessage()).getMessage());
                    // e.rethrowIfKeyUnset(enumKey);
                }

                validatedValue.set(newValidatedInput);
                previousInput = newInput;
                isInitialized.set(true);
            }
        }
    }
}
