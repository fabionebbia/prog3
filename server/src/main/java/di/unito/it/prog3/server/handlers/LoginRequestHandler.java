package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.requests.Request;
import di.unito.it.prog3.server.storage.EmailStore;

public class LoginRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Request request) {
        String user = request.getUser();
        if (emailStore.userExists(user)) {
            return Response.SUCCESS;
        } else {
            return Response.failure("Unknown user " + user);
        }
    }

    @Override
    public void validate(Request request) throws RequestException {

    }

}
