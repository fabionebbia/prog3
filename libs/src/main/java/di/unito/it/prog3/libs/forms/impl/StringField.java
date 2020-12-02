package di.unito.it.prog3.libs.forms.impl;

import di.unito.it.prog3.libs.forms.Field;
import di.unito.it.prog3.libs.forms.FieldValidator;
import di.unito.it.prog3.libs.forms.Validators;
import javafx.scene.control.TextInputControl;

public class StringField extends Field<String> {

    public StringField(TextInputControl control, FieldValidator<String, String> validator, String initialValue) {
        super(control, validator, initialValue);
    }

    public StringField(TextInputControl control, String initialValue) {
        this(control, Validators.ANY_STRING, initialValue);
    }

    public StringField(TextInputControl control) {
        this(control, null);
    }


    public static class NonBlankStringField extends StringField {
        public NonBlankStringField(TextInputControl control, String initialValue) {
            super(control, Validators.NON_BLANK_STRING, initialValue);
        }

        public NonBlankStringField(TextInputControl control) {
            this(control, null);
        }
    }


    public static class EmailField extends StringField {
        public EmailField(TextInputControl control, String initialValue) {
            super(control, Validators.EMAIL, initialValue);
        }

        public EmailField(TextInputControl control) {
            this(control, null);
        }
    }

}
