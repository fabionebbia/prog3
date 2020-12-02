package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.utils.Log;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

import java.util.*;

public class Form<K extends Enum<K>> {

    private final Map<Key<K>, Field<?>> fields;
    private Callback submitCallback;
    private Control submitControl;

    public Form() {
        fields = new LinkedHashMap<>();
    }

    /*
    public <V> FormKey<V> add(TextInputControl control, String initInput, FieldValidator<String, V> validator) {
        return add(new Field<>(control, validator, initInput));
    }

    public <V> FormKey<V> add(Field<V> field) {
        return new FormKey<>(field);
    }

    return new FormKey<>() {
        @Override
        Field<V> getField() {
            return field;
        }
    };
    */

    public <V> Field<V> add(K enumKey, Field<V> field) {
        Key<K> key = new Key<>(enumKey);

        if (fields.containsKey(key)) {
            throw new FormException("Already managed field " + key);
        }

        TextInputControl newControl = field.getControl();
        for (Field<?> managedField : fields.values()) {
            if (newControl.equals(managedField.getControl())) {
                throw new FormException("Control already associated to " + key);
            }
        }

        fields.put(key, field);

        return field;
    }

    public void computeFocus() {
        Control toBeFocused = fields.values().stream()
                .filter(Field::needsFocus)
                .map(f -> (Control) f.getControl())
                .findFirst()
                .orElse(submitControl);

        if (toBeFocused != null) {
            Platform.runLater(toBeFocused::requestFocus);
        }
    }

    /* Just like OpenJFX's FXMLLoader official implementation
    // https://github.com/openjdk/jfx/blob/master/modules/javafx.fxml/src/main/java/javafx/fxml/FXMLLoader.java
    @SuppressWarnings("unchecked")
    public <C> C getFocus(E key) {
        return (C) fields.get(key).getField().getControl();
    }*/

    public void setSubmitControl(Control submitControl) {
        Objects.requireNonNull(submitControl);

        this.submitControl = submitControl;
    }

    public void onSubmit(Callback submitCallback) {
        Objects.requireNonNull(submitCallback);

        if (this.submitCallback != null) {
            Log.warn("Submit callback is being replaced");
        }

        this.submitCallback = submitCallback;
    }

    public void submit() {
        if (submitCallback == null) {
            throw new IllegalStateException("Submit callback unset");
        }

        submitCallback.call();

        computeFocus();
    }

    public static class Key<E extends Enum<E>> {
        private E enumKey;

        private Key(E enumKey) {
            this.enumKey = enumKey;
        }

        public E getEnumKey() {
            return enumKey;
        }

        @Override
        public String toString() {
            return enumKey.name().replaceAll("_+", " ").toLowerCase();
        }
    }

}
