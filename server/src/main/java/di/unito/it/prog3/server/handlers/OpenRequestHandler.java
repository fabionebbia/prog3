package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net2.OpenRequest;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.storage.EmailStore;

public class OpenRequestHandler extends RequestHandler<OpenRequest> {

    public OpenRequestHandler() {
        super(OpenRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, Logger logger, OpenRequest request) throws Exception {
        /*Email.ID emailId = request.getId();
        Email email = emailStore.read(emailId);
        email.setRead(true);
        emailStore.update(email);*/

        Email.ID id = request.getId();;
        emailStore.readAndUpdate(id, Email::setRead);

        return Response.success();
    }

}
