package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net2.Request;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public abstract class RequestHandler<R extends Request> {

    private final Class<R> requestClass;

    public RequestHandler(Class<R> requestClass) {
        this.requestClass = requestClass;
    }

    public abstract Response handle(EmailStore emailStore, Logger logger, R request) throws Exception;

    public Response execute(EmailStore emailStore, Logger logger, Request rawRequest) {
        try {
            R request = requestClass.cast(rawRequest);

            String user = request.getUser();
            if (!emailStore.userExists(user)) {
                return Response.failure("Unknown user " + user);
            }

            request.validate();

            return handle(emailStore, logger, request);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.failure(e);
        }
    }

}
