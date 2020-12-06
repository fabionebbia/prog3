package di.unito.it.prog3.libs.forms.v2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class FieldSpec {

    private final Field field;
    private final Class<?> type;
    private final List<Constraint> constraints;

    protected FieldSpec(Field field, Class<?> type) {
        this.type = type;
        this.field = field;

        constraints = new ArrayList<>();
    }

    protected Field getField() {
        return field;
    }

    protected Class<?> getType() {
        return type;
    }

    protected List<Constraint> getConstraints() {
        return constraints;
    }

    protected void setConstraints(Deque<Constraint> constraints) {
        this.constraints.addAll(constraints);
    }

}
