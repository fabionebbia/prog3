package di.unito.it.prog3.libs.forms.impl;

import di.unito.it.prog3.libs.forms.FieldValidator;
import di.unito.it.prog3.libs.forms.Field;
import di.unito.it.prog3.libs.forms.FormException;
import di.unito.it.prog3.libs.forms.Validators;
import javafx.scene.control.TextInputControl;

public class IntegerField extends Field<Integer> {

    IntegerField(TextInputControl control, FieldValidator<String, Integer> validator, String initialValue) {
        super(control, validator, initialValue);
    }

    public IntegerField(TextInputControl control, String initialValue) {
        this(control, Validators.INTEGER, initialValue);
    }

    public IntegerField(TextInputControl control) {
        this(control, null);
    }


    public static class BoundedIntegerField extends IntegerField {
        public BoundedIntegerField(TextInputControl control, int min, int max, String initialValue) {
            super(control, Validators.INTEGER.and(value -> {

                if (min <= value && value <= max) {
                    return value;
                } else throw new FormException("Field value (" + value + ") out of bounds,"
                        + "should be [" + min + ", " + max + "]");

            }), initialValue);
        }

        public BoundedIntegerField(TextInputControl control, int min, int max) {
            this(control, min, max, null);
        }
    }

}
