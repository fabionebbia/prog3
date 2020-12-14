package di.unito.it.prog3.libs.net;

import di.unito.it.prog3.libs.utils.ObjectCallback;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RequestBuilder<R extends Request> {

    private final Consumer<RequestBuilder<R>> commitConsumer;
    private ResponseHandler successHandler;
    private ResponseHandler failureHandler;

    protected R request;

    RequestBuilder(Supplier<R> requestSupplier, Consumer<RequestBuilder<R>> commitConsumer) {
        this.commitConsumer = commitConsumer;
        request = requestSupplier.get();
    }

    public void setUser(String user) {
        request.user = user;
    }

    public void commit() {
        commitConsumer.accept(this);
    }

    public R getRequest() {
        return request;
    }

    public ResponseHandler getSuccessHandler() {
        return successHandler;
    }

    public RequestBuilder<R> setSuccessHandler(ResponseHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public ResponseHandler getFailureHandler() {
        return failureHandler;
    }

    public RequestBuilder<R> setFailureHandler(ResponseHandler failureHandler) {
        this.failureHandler = failureHandler;
        return this;
    }

}
