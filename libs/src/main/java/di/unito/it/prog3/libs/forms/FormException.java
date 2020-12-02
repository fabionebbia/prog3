package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.utils.Utils;

public class FormException extends IllegalArgumentException {

    public FormException(String message) {
        super(message);
    }

    <E extends Enum<E>> FormException(E fieldEnum, Field<?> field, String message) {
        this("Invalid " + Utils.toTitleCase(fieldEnum) + " field on input " + field.getInput() + ": " + message);
    }

    <E extends Enum<E>> FormException(E fieldEnum, Field<?> field) {
        this("Invalid " + Utils.toTitleCase(fieldEnum) + " field on input " + field.getInput());
    }

    <E extends Enum<E>> FormException(E fieldEnum, String message) {
        this("Invalid " + Utils.toTitleCase(fieldEnum) + " field: " + message);
    }

    <E extends Enum<E>> FormException(E fieldEnum) {
        this("Invalid " + Utils.toTitleCase(fieldEnum) + " field");
    }

}
