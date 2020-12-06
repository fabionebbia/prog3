package di.unito.it.prog3.libs.forms.v2;

import java.lang.reflect.Field;

public class IntegerField extends FormField2 {

    protected IntegerField(Field classField, Constraint[] constraints) {
        super(classField, constraints);
    }

    @Override
    protected Object getRefined(String input) {
        return Integer.parseInt(input);
    }
}
