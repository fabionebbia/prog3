package di.unito.it.prog3.libs.forms;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.forms.FormException.InvalidValueException;
import di.unito.it.prog3.libs.utils.Log;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Form<K extends Enum<K>> {

    @JsonValue
    private final Map<K, Field<?>> fields;

    private Control submitControl;

    private final ObjectMapper json;

    public Form() {
        fields = new LinkedHashMap<>();
        json = new ObjectMapper();
    }

    public Field<String> registerString(K k, TextInputControl c, String initialValue) {
        return register(k, c, s -> s, initialValue);
    }

    public Field<String> registerString(K k, TextInputControl c) {
        return registerString(k, c, null);
    }

    public Field<String> registerNonBlankString(K k, TextInputControl c, String initialValue) {
        return register(k, c, this::ensureNonBlank, initialValue);
    }

    public Field<String> registerNonBlankString(K k, TextInputControl c) {
        return registerNonBlankString(k, c, null);
    }

    public Field<String> registerEmail(K k, TextInputControl c, String initialValue) {
        return register(k, c, this::ensureEmail, initialValue);
    }

    public Field<String> registerEmail(K k, TextInputControl c) {
        return registerEmail(k, c, null);
    }

    public Field<Integer> registerInteger(K k, TextInputControl c, String initialValue) {
        return register(k, c, this::ensureInteger, initialValue);
    }

    public Field<Integer> registerInteger(K k, TextInputControl c, int initialValue) {
        return registerInteger(k, c, String.valueOf(initialValue));
    }

    public Field<Integer> registerInteger(K k, TextInputControl c) {
        return registerInteger(k, c, null);
    }

    public Field<Integer> registerBoundedInteger(K k, TextInputControl c, int min, int max, String initialValue) {
        return register(k, c, s -> ensureBoundedInteger(s, min, max), initialValue);
    }

    public Field<Integer> registerBoundedInteger(K k, TextInputControl c, int min, int max, int initialValue) {
        return registerBoundedInteger(k, c, min, max, String.valueOf(initialValue));
    }

    public Field<Integer> registerBoundedInteger(K k, TextInputControl c, int min, int max) {
        return register(k, c, s -> ensureBoundedInteger(s, min, max), null);
    }

    // TODO initial value is ignored
    private <V> Field<V> register(K key, TextInputControl control, Validator<V> validator, String initialValue) {
        if (fields.containsKey(key)) {
            throw new FormException(key, "is already managed");
        }

        for (Field<?> wrapper : fields.values()) {
            if (control.equals(wrapper.getControl())) {
                throw new FormException("Control already associated to",  key);
            }
        }

        Field<V> field = new Field<>(key, control, validator, initialValue);
        fields.put(key, field);
        return field;
    }

    public void setSubmitControl(Control submitControl) {
        Objects.requireNonNull(submitControl);

        this.submitControl = submitControl;
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

    public void submit() {
        try {
            String jsonString = json.writeValueAsString(this);
            System.out.println(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        computeFocus();
    }

    private String ensureNonBlank(String s) {
        if (s != null && !s.isBlank()) {
            return s;
        }
        throw new InvalidValueException("cannot be blank");
    }

    private int ensureInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            throw new InvalidValueException("must be an integer");
        }
    }

    private int ensureBoundedInteger(String s, int min, int max) {
        int value = ensureInteger(s);
        if (min <= value && value <= max) {
            return value;
        }
        throw new InvalidValueException("must be an integer between " + min + " and " + max);
    }

    private String ensureEmail(String s) {
        if (Emails.isWellFormed(s)) {
            return s;
        }
        throw new InvalidValueException("is malformed");
    }

}
