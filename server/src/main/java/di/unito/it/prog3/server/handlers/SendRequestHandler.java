package di.unito.it.prog3.server.handlers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.SendRequest;
import di.unito.it.prog3.server.gui.LogSession;
import di.unito.it.prog3.server.storage.EmailStore;

import java.util.Set;

public class SendRequestHandler extends RequestHandler<SendRequest> {

    public SendRequestHandler() {
        super(SendRequest.class);
    }

    @Override
    public Response handle(EmailStore emailStore, LogSession log, SendRequest request) throws Exception {
       Set<String> recipients = request.getRecipients();

        log.appendln("Checking all users exist");

        // Check all recipients exist
        for (String recipient : recipients) {
            if (!emailStore.userExists(recipient)) {
                return Response.failure("Unknown recipient " + recipient);
            }
        }

        Email email = new Email();

        // Prepare sender copy
        email.setMailbox(request.getUser());
        email.setSender(request.getUser());
        email.setQueue(Queue.SENT);
        email.setRead();
        email.addAllRecipients(request.getRecipients());
        email.setSubject(request.getSubject());
        email.setBody(request.getBody());

        // Commit sender copy and store it
        Email committedSenderCopy = new Email(email);
        emailStore.store(committedSenderCopy);

        log.appendln("Storing sender's copy in their mailbox");

        // Prepare recipients copies
        email.setRelativeId(committedSenderCopy.getRelativeId());
        email.setQueue(Queue.RECEIVED);
        email.setRead(false);

        // Store each recipient's copy
        for (String recipient : recipients) {
            email.setMailbox(recipient);
            emailStore.store(email);
        }

        log.append("Storing recipients' copies in their mailboxes");

        // Return sender's copy back to the sender
        return Response.success(committedSenderCopy);
    }

}
