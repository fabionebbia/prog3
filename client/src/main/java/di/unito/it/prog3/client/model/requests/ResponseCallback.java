package di.unito.it.prog3.client.model.requests;

public interface ResponseCallback<R extends Response> {

    void call(R response);

}
