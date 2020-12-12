package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

import java.util.List;

public class ReadRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception {
        Email.ID offset = request.getId();

        List<Email> emails;

        if (request.getQueue() != null) {
            emails = emailStore.readAll(request.getUser(), request.getQueue());
            return Response.success(emails);
        }

        if (offset != null) {
            emails = emailStore.readNewer(offset);
            return Response.success(emails);
        }

        System.out.println("Non doveva arrivare qui");
        return Response.failure("Non doveva arrivare qui");
    }

    /**
     * Chrono == NEWER && id == null && many == 0       invalid
     * Chrono == NEWER && id == id   && many == 0       all e-mail received after id
     * Chrono == NEWER && id == id   && many == 1       exactly id
     * Chrono == NEWER && id == null && many == n       the n most recent e-mails
     * @param request
     * @throws RequestException
     */
    @Override
    public void validate(Request request) throws RequestException {

    }
}
