package di.unito.it.prog3.libs.forms;

// Just like OpenJDK's official Java Standard Library implementation
// http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/java/util/function/BiFunction.java
public interface FieldValidator<F extends Field<V>, V, R> {

    R validate(Field<V> field);

    default FieldValidator<V, R> and(FieldValidator<R, R> after) {
        return (V value) -> after.validate(validate(value));
    }

    default <T> FieldValidator<V, T> thenConvert(FieldValidator<R, T> converter) {
        return (V value) -> converter.validate(validate(value));
    }

}
