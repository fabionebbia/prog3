package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public class LoginRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) {
        // If execution got here, the user exists therefore the login is successful
        return Response.success();
    }

    @Override
    public void validate(Request request) throws RequestException {

    }

}
