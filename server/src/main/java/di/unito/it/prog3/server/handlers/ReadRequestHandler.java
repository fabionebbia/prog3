package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Chrono;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

import java.util.List;

public class ReadRequestHandler implements RequestHandler {

    @Override
    public Response handle(EmailStore emailStore, Logger logger, Request request) throws Exception {
        // Email.ID offset = request.getId();

        List<Email> emails;

        // Scaricamento mailbox completa
       /* if (request.getPivot() == null && request.getQueue() == null) {
            emails = emailStore.readAll(request.getUser());
        } else { // Aggiornamento di una singola queue (AKA polling)
            emails = emailStore.readAll(request.getUser(), request.getQueue());
        }*/

        if (request.getQueue() == null) {
            emails = emailStore.readAll(request.getUser());
        } else {
            emails = emailStore.read(
                    request.getDirection(),
                    request.getPivot(),
                    request.getUser(),
                    request.getQueue(),
                    10
            );
        }
/*
        if (request.getQueue() != null) {
            emails = emailStore.readAll(request.getUser(), request.getQueue());
            return Response.success(emails);
        } else if (offset != null) {
            emails = emailStore.read(
                    Chrono.NEWER,
                    request.getPivot(),
                    request.getUser(),
                    request.getQueue(),
                    0);//.readNewer(offset);
            return Response.success(emails);
        } else {
            emails = emailStore.readAll(request.getUser());
            return Response.success(emails);
        }*/
        return Response.success(emails);
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
