package di.unito.it.prog3.libs.forms;

public interface FieldErrorHandler<V> {

    void handle(OldField<V> field);

}
