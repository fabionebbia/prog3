package di.unito.it.prog3.libs.net2;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.ValueCallback;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RequestBuilder<R extends Request> {

    private final Consumer<RequestBuilder<R>> commitConsumer;
    private ValueCallback<Response> onSuccessCallback;
    private ValueCallback<Response> onFailureCallback;

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

    public ValueCallback<Response> getOnSuccessCallback() {
        return onSuccessCallback;
    }

    public RequestBuilder<R> setOnSuccessCallback(ValueCallback<Response> onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
        return this;
    }

    public ValueCallback<Response> getOnFailureCallback() {
        return onFailureCallback;
    }

    public RequestBuilder<R> setOnFailureCallback(ValueCallback<Response> onFailureCallback) {
        this.onFailureCallback = onFailureCallback;
        return this;
    }

}
