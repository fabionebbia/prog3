package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.LoginRequest;
import di.unito.it.prog3.server.gui.LogSession;
import di.unito.it.prog3.server.storage.EmailStore;

public class LoginRequestHandler extends RequestHandler<LoginRequest> {

    public LoginRequestHandler() {
        super(LoginRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, LogSession log, LoginRequest request) {
        // If execution got here, the user exists therefore the login is successful
        return Response.success();
    }

}
