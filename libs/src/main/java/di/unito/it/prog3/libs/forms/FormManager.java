package di.unito.it.prog3.libs.forms;

import com.fasterxml.jackson.databind.ObjectMapper;
import di.unito.it.prog3.libs.forms.FormException.InvalidValueException;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class FormManager<F extends Form, K extends Enum<K>> {

    private final Map<K, FormField<?>> fields;
    private final ObjectMapper json;
    private SubmitCallback<F> submitCallback;
    private Control submitControl;

    private final FormClassDriver<K, F> formClassDriver;

    public FormManager(Class<F> formClass) {
        this.formClassDriver = new FormClassDriver<>(formClass);
        json = new ObjectMapper();
        fields = new LinkedHashMap<>();

    }

    public FormField<String> registerString(K k, TextInputControl c, String initialText) {
        return register(k, String.class, c, s -> s).initialize(initialText);
    }

    public FormField<String> registerString(K k, TextInputControl c) {
        return registerString(k, c, "");
    }


    public FormField<String> registerNonBlankString(K k, TextInputControl c, String initialText) {
        return register(k, String.class, c, this::ensureNonBlank).initialize(initialText);
    }

    public FormField<String> registerNonBlankString(K k, TextInputControl c) {
        return registerNonBlankString(k, c, "");
    }


    public FormField<String> registerEmail(K k, TextInputControl c, String initialText) {
        return register(k, String.class, c, this::ensureEmail).initialize(initialText);
    }

    public FormField<String> registerEmail(K k, TextInputControl c) {
        return registerEmail(k, c, "");
    }


    public FormField<Integer> registerInteger(K k, TextInputControl c, String initialText) {
        return register(k, Integer.class, c, this::ensureInteger).initialize(initialText);
    }

    public FormField<Integer> registerInteger(K k, TextInputControl c, int initialValue) {
        return registerInteger(k, c, String.valueOf(initialValue));
    }

    public FormField<Integer> registerInteger(K k, TextInputControl c) {
        return registerInteger(k, c, "");
    }


    public FormField<Integer> registerBoundedInteger(K k, TextInputControl c, int min, int max, String initialText) {
        return register(k, Integer.class, c, s -> ensureBoundedInteger(s, min, max)).initialize(initialText);
    }

    public FormField<Integer> registerBoundedInteger(K k, TextInputControl c, int min, int max, int initialValue) {
        return registerBoundedInteger(k, c, min, max, String.valueOf(initialValue));
    }

    public FormField<Integer> registerBoundedInteger(K k, TextInputControl c, int min, int max) {
        return registerBoundedInteger(k, c, min, max, "");
    }


    private <V> FormField<V> register(K key, Class<V> type, TextInputControl control, Validator<V> validator) {
        ensureUnmanaged(key, control);
        formClassDriver.registerKeyOrFail(key, type);
        FormField<V> field = new FormField<>(key, control, validator);
        fields.put(key, field);
        return field;
    }

    public void setSubmitControl(Control submitControl) {
        Objects.requireNonNull(submitControl);

        this.submitControl = submitControl;
    }

    public void onSubmit(SubmitCallback<F> submitCallback) {
        Objects.requireNonNull(submitCallback);

        if (this.submitCallback != null) {
            throw new IllegalStateException("Submit callback already set");
        }

        this.submitCallback = submitCallback;
    }

    public void computeFocus() {
        Control toBeFocused = fields.values().stream()
                .filter(FormField::needsFocus)
                .map(f -> (Control) f.getControl())
                .findFirst()
                .orElse(submitControl);

        if (toBeFocused != null) {
            Platform.runLater(toBeFocused::requestFocus);
        }
    }

    public void submit() {
        if (submitCallback == null) {
            throw new IllegalStateException("A submit callback must be specified before submit");
        }

        boolean canSubmit = fields.values().stream()
                .map(FormField::commit)
                .reduce(true, (a, b) -> a && b);

        if (canSubmit) {
            F committedForm;

            try (FormClassDriver<K, F>.FormCommit commit = formClassDriver.newCommit()) {
                for (Map.Entry<K, FormField<?>> entry : fields.entrySet()) {
                    commit.set(entry.getKey(), entry.getValue());
                }
                committedForm = commit.done();
            } catch (FormCommitException e) {
                throw new FormException(e);
            }

            submitCallback.call(committedForm);
        }

        computeFocus();
    }

    private void ensureKeyUnmanaged(K key) {
        if (fields.containsKey(key)) {
            throw new FormException(key, "is already managed");
        }
    }

    private void ensureControlUnmanaged(TextInputControl control) {
        for (Map.Entry<K, FormField<?>> entry : fields.entrySet()) {
            if (control.equals(entry.getValue().getControl())) {
                throw new FormException("Control already associated to",  entry.getKey());
            }
        }
    }

    private void ensureUnmanaged(K key, TextInputControl control) {
        ensureKeyUnmanaged(key);
        ensureControlUnmanaged(control);
    }


    private String ensureNonBlank(String s) {
        if (s != null && !s.isBlank()) {
            return s;
        }
        throw new InvalidValueException("cannot be blank");
    }

    private int ensureInteger(String s) {
        ensureNonBlank(s);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            throw new InvalidValueException("must be an integer");
        }
    }

    private int ensureBoundedInteger(String s, int min, int max) {
        ensureNonBlank(s);
        int value = ensureInteger(s);
        if (min <= value && value <= max) {
            return value;
        }
        throw new InvalidValueException("must be an integer (" + min + "-" + max + ")");
    }

    private String ensureEmail(String s) {
        ensureNonBlank(s);
        if (Emails.isWellFormed(s)) {
            return s;
        }
        throw new InvalidValueException("is malformed");
    }

}
