package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.DeletionRequest;
import di.unito.it.prog3.server.gui.LogSession;
import di.unito.it.prog3.server.storage.EmailStore;

public class DeletionRequestHandler extends RequestHandler<DeletionRequest> {

    public DeletionRequestHandler() {
        super(DeletionRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, LogSession log, DeletionRequest request) throws Exception {
        Email.ID id = request.getId();

        log.append("Deleting e-mail " + request.getId());

        emailStore.delete(id);

        return Response.success();
    }


}
