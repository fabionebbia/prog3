package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public class DeletionRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception {
        emailStore.delete(request.getId());

        return Response.success();
    }

    @Override
    public void validate(Request request) throws RequestException {

    }

}
