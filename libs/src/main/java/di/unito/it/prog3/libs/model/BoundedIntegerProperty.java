package di.unito.it.prog3.libs.model;

import java.util.function.Predicate;

public class BoundedIntegerProperty extends ConstrainedIntegerProperty {

    public BoundedIntegerProperty(int min, int max, int initialValue) {
        super(ensureProperBounds(min, max), initialValue);
    }

    public BoundedIntegerProperty(int min, int max) {
        this(min, max, min);
    }

    private static Predicate<Integer> ensureProperBounds(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min (" + min + ") cannot be greater than max (" + max + ")");
        } else {
            return (Integer v) -> min <= v && v <= max;
        }
    }
}
