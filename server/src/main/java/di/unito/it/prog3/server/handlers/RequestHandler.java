package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public interface RequestHandler {

    Response handle(EmailStore emailStore, Logger logger, Request request);

    void validate(Request request) throws RequestException;

    default Response execute(EmailStore emailStore, Logger logger, Request request) {
        try {
            String user = request.getUser();
            logger.info("Checking user existence " + emailStore.userExists(user));
            if (!emailStore.userExists(user)) {
                return Response.failure("Unknown user " + user);
            }
            logger.info("User exist");
            validate(request);
            Response response = handle(emailStore, logger, request);
            logger.info("Done");
            logger.log(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.failure(e);
        }
    }

    default void ensure(boolean condition, String exceptionMessage) throws RequestException {
        if (!condition) throw new RequestException(exceptionMessage);
    }

}
