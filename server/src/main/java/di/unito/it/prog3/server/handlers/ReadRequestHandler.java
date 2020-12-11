package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Chrono;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

import java.util.ArrayList;
import java.util.List;

public class ReadRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception {
        Chrono direction = request.getDirection();
        Email.ID offset = request.getId();
        int many = request.getMany();

        if (request.getQueue() != null) {
            List<Email> emails = emailStore.read(request.getUser(), request.getQueue(), many);
            return Response.success(emails);
        }

        System.out.println("Non doveva arrivare qui");

        if (offset != null && many == 1) {
            Email email = emailStore.read(offset);
            return Response.success(email);
        } else {
            List<Email> emails = emailStore.read(direction, offset, many);
            return Response.success(emails);
        }
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
