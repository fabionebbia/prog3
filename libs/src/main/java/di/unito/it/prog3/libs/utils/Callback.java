package di.unito.it.prog3.libs.utils;

public interface Callback {

    void call();

    interface TypedRequest<T> {
        T perform();
    }

    interface RequestWithInput<I, R> {
        R perform(I input);
    }

    interface TypedCallback<T> {
        void call(T response);
    }

}
