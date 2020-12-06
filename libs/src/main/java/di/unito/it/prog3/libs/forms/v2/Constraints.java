package di.unito.it.prog3.libs.forms.v2;

import di.unito.it.prog3.libs.utils.Emails;

public final class Constraints {

    private Constraints() {}

    public static final Constraint REQUIRED = input -> input != null && !input.isBlank();

    public static final Constraint EMAIL = Emails::isWellFormed;

    public static Constraint INTEGER(int min, int max) {
        return (String input) -> {
            try {
                int value = Integer.parseInt(input);
                return min <= value && value <= max;
            } catch (NumberFormatException ignored) {}
            return false;
        };
    }

}
