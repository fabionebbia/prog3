package di.unito.it.prog3.libs.model;

import di.unito.it.prog3.libs.utils.Emails;

import java.util.function.Predicate;

public class EmailStringProperty extends ConstrainedStringProperty {

    private static final Predicate<String> CONSTRAINT = Emails::isWellFormed;

    public EmailStringProperty(String initialValue) {
        super(CONSTRAINT, initialValue);
    }

    public EmailStringProperty() {
        super(CONSTRAINT);
    }
}
