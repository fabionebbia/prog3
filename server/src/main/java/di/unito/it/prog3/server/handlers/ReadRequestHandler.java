package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Chrono;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.ReadRequest;
import di.unito.it.prog3.libs.utils.Utils;
import di.unito.it.prog3.server.gui.LogSession;
import di.unito.it.prog3.server.storage.EmailStore;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReadRequestHandler extends RequestHandler<ReadRequest> {

    public ReadRequestHandler() {
        super(ReadRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, LogSession log, ReadRequest request) throws Exception {
        // Email.ID offset = request.getId();

        List<Email> emails;

        if (request.getQueue() == null) {
            log.append("Reading queues [");

            List<String> queues = Arrays.stream(Queue.values())
                    .map(Queue::toString)
                    .collect(Collectors.toList());
            log.append(Utils.join(queues, ", "));

            log.appendln("]");

            emails = emailStore.readAll(request.getUser());
        } else {
            Chrono direction = request.getDirection();
            Queue queue = request.getQueue();
            LocalDateTime pivot = request.getPivot();
            int many = request.getMany() > 0 ? request.getMany() : Integer.MAX_VALUE;

            log.append("Reading " + direction.name().toLowerCase());
            log.appendln(" e-mails from " + queue.name() + " queue");

            emails = emailStore.read(
                    request.getDirection(),
                    request.getPivot(), // != null ? request.getPivot() : LocalDateTime.now(),
                    request.getUser(),
                    request.getQueue(),
                    request.getMany() > 0 ? request.getMany() : Integer.MAX_VALUE
            );
        }

        log.append("Found " + emails.size() + ", sending response to client..");
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
