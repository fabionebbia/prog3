package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.utils.Utils;

public class FormException extends IllegalArgumentException {

    public FormException(String message) {
        super(message);
    }

    FormException(Enum<?> key, String message) {
        // this("Invalid " + fieldEnum + " field: " + message);
        this(getFieldName(key) + " " + message);
    }

    FormException(String message, Enum<?> key) {
        this(message + " " + getFieldName(key));
    }

    private static String getName(Enum<?> key) {
        return key.name().replaceAll("_+", " ").toLowerCase();
    }

    private static String getFieldName(Enum<?> key) {
        return getName(key) + " field";
    }


    public static class InvalidValueException extends IllegalArgumentException {
        private final boolean hasKey;

        InvalidValueException(String message) {
            super(message);
            hasKey = false;
        }

        InvalidValueException(Enum<?> key, String message) {
            super(Utils.toUpperFirst(FormException.getName(key)) + " " + message);
            hasKey = true;
        }

        public void rethrowIfKeyUnset(Enum<?> enumKey) throws InvalidValueException{
            if (!hasKey) {
                throw new InvalidValueException(enumKey, getMessage());
            }
        }
    }

}
