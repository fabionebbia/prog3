package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net2.LoginRequest;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public class LoginRequestHandler extends RequestHandler<LoginRequest> {

    public LoginRequestHandler() {
        super(LoginRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, Logger logger, LoginRequest request) {
        // If execution got here, the user exists therefore the login is successful
        emailStore.concurrencyTest();
        return Response.success();
    }

}
