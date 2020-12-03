package di.unito.it.prog3.libs.forms;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.scene.control.TextInputControl;

public class OldField<V> {

    private final StringProperty textProperty;
    private final BooleanProperty initialized;
    private final TextInputControl control;
    private final ObjectProperty<V> value;

    private FieldErrorHandler<V> errorHandler;
    private Object key;

    public OldField(TextInputControl control) {
        this.control = control;

        value = new SimpleObjectProperty<>();
        initialized = new SimpleBooleanProperty();

        textProperty = control.textProperty();

        // resets control's text when the user corrects the input after a previous error
        textProperty.addListener((observable, oldValue, newValue) -> {
            initialized.set(true);

            if (isInvalid().get()) {
                textProperty.set(null);
            }
        });
    }

    public OldField(TextInputControl control, String initText) {
        this(control);
        initialize(initText);
    }

    public void initialize(String text) {
        if (initialized.get()) {
            throw new IllegalStateException("Field " + key + " already initialized");
        }
        textProperty.set(text);
        validate();
    }

    public void setErrorHandler(FieldErrorHandler<V> errorHandler) {
        this.errorHandler = errorHandler;
    }

    protected final void validate() {
        String input = textProperty.get();

        try {
            V validated = null;
            value.set(validated);
        } catch (FormException e) {
            if (errorHandler != null) {
                errorHandler.handle(this);
            } else {
                throw e;
            }
        }
    }

    protected final TextInputControl getControl() {
        return control;
    }

    public String getInput() {
        return textProperty.get();
    }

    public BooleanBinding isValid() {
        return initialized.and(value.isNotNull());
    }

    public BooleanBinding isInvalid() {
        return isValid().not();
    }

    public BooleanBinding isInitialized() {
        return Bindings.createBooleanBinding(initialized::get, initialized);
    }

    protected boolean needsFocus() {
        return isInitialized().not().or(isInvalid()).get();
    }


}
