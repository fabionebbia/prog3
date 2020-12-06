package di.unito.it.prog3.libs.forms.v2;

import di.unito.it.prog3.libs.forms.v2.annotations.Bounded;
import di.unito.it.prog3.libs.forms.v2.annotations.Email;
import di.unito.it.prog3.libs.forms.v2.annotations.Optional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

class FormLoader {

    private final Class<?> formClass;

    protected FormLoader(Class<?> formClass) {
        this.formClass = formClass;
    }

    protected List<FormField2> load() {
        List<FormField2> keys = new ArrayList<>();
        for (Field classField : formClass.getDeclaredFields()) {
            Constraint[] constraints = resolveConstraints(classField);
            Class<?> type = classField.getType();

            FormField2 field;
            if (type == String.class) {
                field = new StringField(classField, constraints);
            } else if (type == Integer.class) {
                field = new IntegerField(classField, constraints);
            } else {
                throw new RuntimeException(); // TODO throw unsupported field type
            }

            keys.add(field);
        }

        if (keys.isEmpty()) {
            // TODO empty form
        }

        return keys;
    }

    private Constraint[] resolveConstraints(Field classField) {
        Deque<Constraint> constraints = new LinkedList<>();

        Optional optional = classField.getAnnotation(Optional.class);
        Bounded bounded = classField.getAnnotation(Bounded.class);
        Email email = classField.getAnnotation(Email.class);

        if (classField.getType() == Integer.class) {
            ensureMissing(Integer.class, email);

            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;

            if (bounded != null) {
                min = bounded.min();
                max = bounded.max();

                if (max < min) {
                    // TODO throw new something
                }
            }

            constraints.add(Constraints.INTEGER(min, max));
        }

        if (classField.getType() == String.class) {
            ensureMissing(String.class, bounded);

            if (email != null) {
                constraints.add(Constraints.EMAIL);
            }
        }

        if (optional == null) {
            constraints.addFirst(Constraints.REQUIRED);
        }

        return constraints.toArray(new Constraint[]{});
    }

    private void ensureMissing(Class<?> type, Annotation... annotations) {
        Arrays.asList(annotations).forEach(a -> {
            if (a != null) {
                // TODO throw exception
            }
        });
    }
}
