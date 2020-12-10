package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.requests.Request;
import di.unito.it.prog3.server.storage.EmailStore;

public interface RequestHandler {

    Response handle(EmailStore emailStore, Request request);

    void validate(Request request) throws RequestException;

    default Response execute(EmailStore emailStore, Request request) {
        try {
            validate(request);
            return handle(emailStore, request);
        } catch (RequestException e) {
            e.printStackTrace();
            return Response.failure(e);
        }
    }

}
