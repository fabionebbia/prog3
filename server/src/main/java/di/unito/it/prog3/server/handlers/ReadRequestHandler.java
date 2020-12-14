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
        String user = request.getUser();
        List<Email> emails;

        if (request.getQueue() == null) {
            log.append("Reading queues [");

            List<String> queues = Arrays.stream(Queue.values())
                    .map(Queue::toString)
                    .collect(Collectors.toList());
            log.append(Utils.join(queues, ", "));

            log.appendln("]");

            emails = emailStore.readAll(user);
        } else {
            Chrono direction = request.getDirection();
            Queue queue = request.getQueue();
            LocalDateTime pivot = request.getPivot();
            int many = request.getMany() > 0 ? request.getMany() : Integer.MAX_VALUE;

            log.append("Reading " + direction.name().toLowerCase());
            log.appendln(" e-mails from " + queue.name() + " queue");

            emails = emailStore.read(direction, pivot, user, queue, many);
        }

        log.append("Found " + emails.size() + ", sending response to client..");

        return Response.success(emails);
    }

}
