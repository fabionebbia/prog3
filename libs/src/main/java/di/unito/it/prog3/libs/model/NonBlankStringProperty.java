package di.unito.it.prog3.libs.model;

import java.util.function.Predicate;

public class NonBlankStringProperty extends ConstrainedStringProperty {

    private static final Predicate<String> CONSTRAINT = s -> s != null && !s.isBlank();

    public NonBlankStringProperty(String initialValue) {
        super(CONSTRAINT, initialValue);
    }

    public NonBlankStringProperty() {
        super(CONSTRAINT);
    }
}
