package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.server.gui.LogSession;
import di.unito.it.prog3.server.storage.EmailStore;

public abstract class RequestHandler<R extends Request> {

    /**
     * The class (type) of the request handled by this handler
     */
    private final Class<R> requestClass;


    public RequestHandler(Class<R> requestClass) {
        this.requestClass = requestClass;
    }


    /**
     * Contains the logic needed to handle the request.
     *
     * @param emailStore The e-mail store for store operations.
     * @param log The log session associated to the current request.
     * @param request The request.
     * @return The response of handling succeeds.
     * @throws Exception If handling fails.
     */
    public abstract Response handle(EmailStore emailStore, LogSession log, R request) throws Exception;


    /**
     * Validates the request, checks the user exists and if it does handles the request.
     *
     * @param emailStore The e-mail store for store operations.
     * @param log The log session associated to the current request.
     * @param rawRequest The request.
     * @return The produced response.
     */
    public Response execute(EmailStore emailStore, LogSession log, Request rawRequest) {
        try {
            R request = requestClass.cast(rawRequest);

            request.validate();

            String user = request.getUser();
            if (!emailStore.userExists(user)) {
                return Response.failure("Unknown user " + user);
            }

            return handle(emailStore, log, request);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.failure(e);
        }
    }

}
