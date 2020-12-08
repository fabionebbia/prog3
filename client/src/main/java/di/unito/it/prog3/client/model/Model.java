package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.EmailProperty;
import di.unito.it.prog3.libs.communication.net.responses.Response;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Mailbox;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.exceptions.MalformedEmailAddressException;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.concurrent.ExecutorService;

public class Model {

    // Login
    private final ReadOnlyStringWrapper serverURL;
    private final ReadOnlyStringWrapper user;

    private final ListProperty<Email> emails;
    private final EmailProperty currentEmail;

    private final Client client;

    private boolean loggedIn;

    private final ExecutorService executor;

    public Model(ExecutorService executor) {
        this.executor = executor;

        client = new Client(this);

        // Login
        serverURL = new ReadOnlyStringWrapper();
        user = new ReadOnlyStringWrapper();

        currentEmail = new EmailProperty();

        Email e = new Email();
        e.setSender(Mailbox.fromString("a@b.c"));
        e.setQueue(Queue.RECEIVED);
        e.setSubject("Subject");
        e.setBody("Body");
        e.setMailbox(Mailbox.fromString("tmp@lacrime.assai"));
        e.timestamp();

        emails = new SimpleListProperty<>(FXCollections.observableArrayList(e));
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

        this.serverURL.set(host + ":" + port);
        this.user.set(user);
        loggedIn = true;

        return response;
    }

    public ReadOnlyObjectProperty<ClientStatus> clientStatusProperty() {
        return client.statusProperty();
    }

    public ListProperty<Email> emailsProperty() {
        return emails;
    }

    public EmailProperty currentEmailProperty() {
        return currentEmail;
    }

    public ReadOnlyStringProperty serverURLProperty() {
        return serverURL.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty userProperty() {
        return user.getReadOnlyProperty();
    }

}
