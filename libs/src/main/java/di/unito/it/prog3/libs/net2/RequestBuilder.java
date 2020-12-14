package di.unito.it.prog3.libs.net2;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.utils.ObjectCallback;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RequestBuilder<R extends Request> {

    private final Consumer<RequestBuilder<R>> commitConsumer;
    private ObjectCallback<Response> onSuccessCallback;
    private ObjectCallback<Response> onFailureCallback;

    protected R request;

    RequestBuilder(Supplier<R> requestSupplier, Consumer<RequestBuilder<R>> commitConsumer) {
        this.commitConsumer = commitConsumer;
        request = requestSupplier.get();
    }

    public void setUser(String user) {
        request.setUser(user);
    }

    public R build() {
        return request;
    }

    public void commit() {
        commitConsumer.accept(this);
    }

    public R getRequest() {
        return request;
    }

    public ObjectCallback<Response> getOnSuccessCallback() {
        return onSuccessCallback;
    }

    public RequestBuilder<R> setOnSuccessCallback(ObjectCallback<Response> onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
        return this;
    }

    public ObjectCallback<Response> getOnFailureCallback() {
        return onFailureCallback;
    }

    public RequestBuilder<R> setOnFailureCallback(ObjectCallback<Response> onFailureCallback) {
        this.onFailureCallback = onFailureCallback;
        return this;
    }

}
