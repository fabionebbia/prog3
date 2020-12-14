package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.OpenRequest;
import di.unito.it.prog3.server.gui.LogSession;
import di.unito.it.prog3.server.storage.EmailStore;

public class OpenRequestHandler extends RequestHandler<OpenRequest> {

    public OpenRequestHandler() {
        super(OpenRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, LogSession log, OpenRequest request) throws Exception {
        /*Email.ID emailId = request.getId();
        Email email = emailStore.read(emailId);
        email.setRead(true);
        emailStore.update(email);*/

        Email.ID id = request.getId();

        log.append("Marking " + id + " as read");

        emailStore.readAndUpdate(id, Email::setRead);

        return Response.success();
    }

}
