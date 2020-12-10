package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Mailbox;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.exceptions.MalformedEmailAddressException;
import di.unito.it.prog3.libs.model.EmailProperty;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.UUID;

public class Model {

    // Login
    private final ReadOnlyStringWrapper server;
    private final ReadOnlyStringWrapper user;

    private final UserQueueHolder userQueueHolder;
    private final EmailProperty currentEmail;

    private final Client client;

    private boolean loggedIn;


    public Model() {
        client = new Client(this);

        // Login
        server = new ReadOnlyStringWrapper();
        user = new ReadOnlyStringWrapper();

        currentEmail = new EmailProperty();

        userQueueHolder = new UserQueueHolder();
    }

    public Response login(String host, int port, String user) {
        if (loggedIn) {
            throw new IllegalStateException("Already logged in");
        }

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("Server address required");
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port number should be between 1-65535");
        }

        if (user == null || user.isBlank()) {
            throw new IllegalArgumentException("User e-mail required");
        }

        if (!Emails.isWellFormed(user)) {
            throw new MalformedEmailAddressException(user);
        }

        Response response = client.login(host, port, user);

        if (response.successful()) {
            Email.ID sentEmailId = Email.ID.fromString("me@email.tld/S/" + UUID.randomUUID().toString());
            Email.ID receivedReplyId = Email.ID.fromString("me@email.tld/R/" + UUID.randomUUID().toString());

            Email sentEmail = new Email();
            sentEmail.setId(sentEmailId);
            sentEmail.setSender(Mailbox.fromString("me@email.tld"));
            sentEmail.setSubject("This is my original message");
            sentEmail.setBody("With its interesting body");
            sentEmail.addRecipient(Mailbox.fromString("someone@email.tld"));
            sentEmail.timestamp();
            sentEmail.addReply(receivedReplyId);

            Email receivedReply = new Email();
            receivedReply.setId(receivedReplyId);
            receivedReply.setSender(Mailbox.fromString("someone@email.tld"));
            receivedReply.setSubject("This is someone else's reply");
            receivedReply.setBody("With its interesting body too");
            receivedReply.addRecipient(Mailbox.fromString("me@email.tld"));
            receivedReply.addRecipient(Mailbox.fromString("whoknows@email.tld"));
            receivedReply.timestamp();
            receivedReply.setReplyOf(sentEmailId);

            ArrayList<Email> tmp = new ArrayList<>();
            tmp.add(sentEmail);
            tmp.add(receivedReply);
            userQueueHolder.bind(new SimpleListProperty<>(FXCollections.observableList(tmp)));
            loggedIn = true;
            this.server.set(host + ":" + port);
            this.user.set(user);
        }

        return response;
    }

    public Client getClient() {
        return client;
    }

    public void setCurrentEmail(Email email) {
        currentEmail.set(email);
    }

    public void unsetCurrentEmail() {
        setCurrentEmail(null);
    }

    public BooleanBinding isCurrentEmailSet() {
        return currentEmail.isNotNull();
    }

    public ReadOnlyObjectProperty<ClientStatus> clientStatusProperty() {
        return client.statusProperty();
    }

    public EmailProperty currentEmailProperty() {
        return currentEmail;
    }

    public ReadOnlyStringProperty serverProperty() {
        return server.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty userProperty() {
        return user.getReadOnlyProperty();
    }

    public ObservableValue<ObservableList<Email>> receivedQueue() {
        return userQueueHolder.getQueue(Queue.RECEIVED);
    }

    public ObservableValue<ObservableList<Email>> sentQueue() {
        return userQueueHolder.getQueue(Queue.SENT);
    }

}
