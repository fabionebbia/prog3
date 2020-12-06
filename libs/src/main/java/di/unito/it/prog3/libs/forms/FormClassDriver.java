package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FormClassDriver<K extends Enum<K>, F extends Form> {

    private final Map<K, Field> fields;
    private final Class<F> formClass;
    private final Field[] classFields;
    private F form;

    FormClassDriver(Class<F> formClass) {
        this.formClass = formClass;

        fields = new HashMap<>();

        classFields = formClass.getDeclaredFields();
    }

    void registerKeyOrFail(K key, Class<?> type) {
        String camelCaseKey = Utils.toCamelCase(key);

        Optional<Field> classField = Arrays.stream(classFields)
                .filter(f -> type.equals(f.getType()) && camelCaseKey.equals(f.getName()))
                .findFirst();

        if (classField.isEmpty()) {
            throw new FormException(
                    formClass.getSimpleName() + " does not contain a field named "
                    + camelCaseKey + " of type " + type.getSimpleName()
                    + " needed to register " + key.name().replaceAll("_+", " ").toLowerCase()
            );
        }

        fields.put(key, classField.get());
    }

    FormCommit newCommit() throws FormCommitException {
        return new FormCommit();
    }

    class FormCommit implements AutoCloseable {

        private F form;

        private FormCommit() throws FormCommitException {
            try {
                form = formClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                throw new FormCommitException("Could not instantiate " + formClass.getSimpleName(), e);
            }
        }

        protected void set(K key, FormField<?> formField) throws FormCommitException {
            try {
                Field classField = fields.get(key);
                classField.setAccessible(true);
                classField.set(form, formField.get());
                classField.setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new FormCommitException("Could not set " + key + " in " + formClass.getSimpleName(), e);
            }
        }

        protected F done() throws FormCommitException {
            if (form == null) {
                throw new FormCommitException("Cannot get closed " + formClass.getSimpleName());
            }
            return form;
        }

        @Override
        public void close() {
            form = null;
        }
    }

}
