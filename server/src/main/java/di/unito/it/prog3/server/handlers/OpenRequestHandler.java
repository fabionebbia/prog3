package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public class OpenRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception {
        /*Email.ID emailId = request.getId();
        Email email = emailStore.read(emailId);
        email.setRead(true);
        emailStore.update(email);*/

        emailStore.readAndUpdate(request.getId(), email -> email.setRead(true));

        return Response.success();
    }

    @Override
    public void validate(Request request) throws RequestException {

    }

}
