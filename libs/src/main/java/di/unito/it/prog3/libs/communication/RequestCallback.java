package di.unito.it.prog3.libs.communication;

public interface RequestCallback<T> {

    void onResponse(T response);

    void onError(Throwable e);

}
