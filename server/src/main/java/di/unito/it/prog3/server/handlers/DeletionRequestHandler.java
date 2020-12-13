package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

import java.util.Set;

public class DeletionRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception {
        Email email = emailStore.read(request.getId());

        String user = request.getUser();

        System.out.println("Requested deletion: " + request.getId());

        if (request.getRecipients().contains(user)) {
            String twinQueue = request.getId().getQueue() ==  Queue.SENT ? "/R/" : "/S/";
            Email.ID twinId = Email.ID.fromString(user + twinQueue + email.getId().getRelativeId());
            emailStore.delete(twinId, true);
            System.out.println("Deleting twin: " + twinId);
        }

        emailStore.delete(request.getId(), false);
        System.out.println("Deleted: " + request.getId());

        return Response.success();
    }

    @Override
    public void validate(Request request) throws RequestException {

    }

}
