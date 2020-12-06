package di.unito.it.prog3.libs.forms.v2;

import di.unito.it.prog3.libs.utils.Utils;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class FormManager2<F extends Form2>  {

    private final Map<String, FormField2> fields;
    private final Class<F> formClass;
    private Control submitControl;

    private final Object submitHandler;
    private final Method handleMethod;

    public FormManager2(Class<F> formClass, Object submitHandler) {
        Method handleMethod = null;
        for (Method method : submitHandler.getClass().getMethods()) {
            if (method.getName().equals("submit")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 /* 2 */
                        && Form2.class != parameterTypes[0]
                        && Form2.class.isAssignableFrom(parameterTypes[0])
                        /*&& Callback2.class.isAssignableFrom(parameterTypes[1])*/) {
                    if (handleMethod == null) {
                        handleMethod = method;
                    } else throw new RuntimeException(); // TODO duplicate handle(SomeForm form)
                }
            }
        }

        if (handleMethod == null) {
            throw new RuntimeException(); // TODO missing handle(SomeForm form) which is impossible given submitHandler implements SubmitHandler
        }

        this.formClass = formClass;
        this.handleMethod = handleMethod;
        this.submitHandler = submitHandler;
        this.fields = new LinkedHashMap<>();

        for (FormField2 field : new FormLoader(formClass).load()) {
            fields.put(field.getKey(), field);
        }
    }

    public void initialize(Enum<?> enumKey, String text) {
        String key = Utils.toCamelCase(enumKey);

        fields.get(key).initialize(text);
    }

    public FormField2 register(Enum<?> enumKey, TextInputControl control) {
        return register(enumKey, control, null);
    }

    public FormField2 register(Enum<?> enumKey, TextInputControl control, String initialText) {
        String key = Utils.toCamelCase(enumKey);

        if (!fields.containsKey(key)) {
            // TODO
        }

        for (Map.Entry<String, FormField2> entry : fields.entrySet()) {
            if (control.equals(entry.getValue().getControl())) {
                // TODO throw new FormException("Control already associated to",  entry.getKey());
            }
        }

        FormField2 field = fields.get(key);
        field.bindControl(control);

        if (initialText != null) {
            field.initialize(initialText);
        }

        return field;
    }

    public void setSubmitControl(Control submitControl) {
        Objects.requireNonNull(submitControl);

        this.submitControl = submitControl;
    }


    public void computeFocus() {
        Control toBeFocused = fields.values().stream()
                .filter(FormField2::needsFocus)
                .map(f -> (Control) f.getControl())
                .findFirst()
                .orElse(submitControl);

        if (toBeFocused != null) {
            Platform.runLater(toBeFocused::requestFocus);
        }
    }

    public F commit() {
        return null;
    }

    public void submit(FormCallback<F> callback) {
        try {
            F committedForm = formClass.getDeclaredConstructor().newInstance();

            boolean canSubmit = fields.values().stream()
                    .map(field -> field.commit(committedForm))
                    .reduce(true, (prev, curr) -> prev && curr);

            if (canSubmit) {
                handleMethod.invoke(submitHandler, committedForm/*, callback*/);
            }
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace(); // TODO throw new FormCommitException("Could not instantiate " + formClass.getSimpleName(), e);
        }

        computeFocus();
    }

}
