package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public class SendRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) {
        logger.info("Handling");
        Email newEmail = new Email();
        newEmail.addAllRecipients(request.getRecipients());
        newEmail.setSubject(request.getSubject());
        newEmail.setBody(request.getBody());

        try {
            emailStore.store(newEmail);
        } catch (Exception e) {
            return Response.failure(e.getMessage()); // "Could not send e-mail");
        }

        return Response.success(newEmail);
    }

    @Override
    public void validate(Request request) throws RequestException {
        ensure(request.getRecipients().size() > 0, "No recipients");
    }

}
