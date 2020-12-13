package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net2.ReadRequest;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class ReadRequestHandler extends RequestHandler<ReadRequest> {

    public ReadRequestHandler() {
        super(ReadRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, Logger logger, ReadRequest request) throws Exception {
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
                    request.getPivot() != null ? request.getPivot() : LocalDateTime.now(),
                    request.getUser(),
                    request.getQueue(),
                    request.getMany() > 0 ? request.getMany() : Integer.MAX_VALUE
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

}
