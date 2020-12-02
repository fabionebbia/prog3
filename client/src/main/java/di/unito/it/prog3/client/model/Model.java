package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseModel;
import di.unito.it.prog3.client.fxml.model.EmailProperty;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Mailbox;
import di.unito.it.prog3.libs.store.EmailStore;
import di.unito.it.prog3.libs.store.EmailStoreException;
import di.unito.it.prog3.libs.store.LocalJsonEmailStore;
import di.unito.it.prog3.libs.store.Queue;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import static di.unito.it.prog3.client.model.Status.*;

public class Model extends BaseModel<Status> {

    private final ListProperty<Email> emails;
    private final EmailProperty currentEmail;
    private final Client client;

    private boolean loggedIn;

    public Model() {
        super(IDLE);

        client = new Client(this);

        Email e = new Email();
        e.setMailbox(Mailbox.fromString("a@b.c"));
        e.setQueue(Queue.RECEIVED);
        e.setSender(Mailbox.fromString("d@e.f"));
        e.addRecipient(Mailbox.fromString("d@e.f"));
        e.setSubject("No subject");
        e.setBody("Body");
        e.timestamp();

        /*EmailStore emailStore = new LocalJsonEmailStore("_store");
        try {
            emailStore.store(e);
        } catch (EmailStoreException emailStoreException) {
            emailStoreException.printStackTrace();
        }*/

        currentEmail = new EmailProperty(e);
        emails = new SimpleListProperty<>(FXCollections.observableArrayList(e));
    }

    public ReadOnlyObjectProperty<ClientStatus> clientStatusProperty() {
        return client.statusProperty();
    }

    public ReadOnlyStringProperty clientStatusMessageProperty() {
        return client.statusMessageProperty();
    }

    public ListProperty<Email> emailsProperty() {
        return emails;
    }

    public EmailProperty currentEmailProperty() {
        return currentEmail;
    }

    public Client getClient() {
        return client;
    }

    public void shutdown() {
        client.shutdown();
    }

}
