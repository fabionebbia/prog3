package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;

public interface RequestHandler {

    Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception;

    void validate(Request request) throws RequestException;

    default Response execute(EmailStore emailStore, Logger logger, Request request) {
        try {
            String user = request.getUser();
            if (!emailStore.userExists(user)) {
                return Response.failure("Unknown user " + user);
            }
            validate(request);
            return handle(emailStore, logger, request);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.failure(e);
        }
    }

    default void ensure(boolean condition, String exceptionMessage) throws RequestException {
        if (!condition) throw new RequestException(exceptionMessage);
    }

}
