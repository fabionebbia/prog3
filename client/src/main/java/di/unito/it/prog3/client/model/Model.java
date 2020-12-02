package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseModel;
import di.unito.it.prog3.client.fxml.model.EmailProperty;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Mailbox;
import di.unito.it.prog3.libs.store.EmailStore;
import di.unito.it.prog3.libs.store.EmailStoreException;
import di.unito.it.prog3.libs.store.LocalJsonEmailStore;
import di.unito.it.prog3.libs.store.Queue;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.Input;
import di.unito.it.prog3.libs.utils.Log;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import static di.unito.it.prog3.client.model.Status.*;

public class ClientModel extends BaseModel<Status> {

    private final ReadOnlyStringWrapper serverAddress;
    private final ReadOnlyStringWrapper emailAddress;
    private final ListProperty<Email> emails;
    private final EmailProperty currentEmail;
    private final Client client;

    public ClientModel(Client client, String server, String username) {
        super(IDLE);
        this.client = client;
        serverAddress = new ReadOnlyStringWrapper(server);
        emailAddress = new ReadOnlyStringWrapper(username);
/*
        Email eee = new Email.EmailBuilder("a@b.c/R/11377")
                .setSender("walter.dambrosio@unito.it")
                .addRecipient("fabio.nebbia@edu.unito.it")
                .setTimestamp(LocalDateTime.now().minus(2, ChronoUnit.YEARS))
                .setSubject("La derivata *è* la pendenza")
                .setBody("Ricordatevelo!")
                .setRead(true)
                .build();

        Email eeee = new Email.EmailBuilder("a@b.c/R/11376")
                .setSender("giulia.nebbia@edu.unito.itasdhqwmcrhgqhrguahiohrehgafhghiaerhgioadsog")
                .addRecipient("fabio.nebbia@edu.unito.it")
                .setTimestamp(LocalDateTime.now().minus(1, ChronoUnit.MONTHS))
                .setSubject("Some very very long subject oh my god")
                .setBody("12 nigiri\n4 tramezzini\n2 involtini primavera fasdjhfi hdsf isajf hsid fgoas gioodsaogiafsfgoha idfiohfi")
                .setRead(true)
                .build();
*/
        Email e = new Email();
        e.setMailbox(Mailbox.fromString("a@b.c"));
        e.setQueue(Queue.RECEIVED);
        e.setSender(Mailbox.fromString("d@e.f"));
        e.addRecipient(Mailbox.fromString("d@e.f"));
        e.setSubject("No subject");
        e.setBody("Body");
        e.timestamp();

        EmailStore emailStore = new LocalJsonEmailStore("_store");
        try {
            emailStore.store(e);
        } catch (EmailStoreException emailStoreException) {
            emailStoreException.printStackTrace();
        }

        currentEmail = new EmailProperty(e);
        emails = new SimpleListProperty<>(FXCollections.observableArrayList(e));
    }

    public void login() {
        String serverAddress = serverAddressProperty().get();
        String emailAddress = emailAddressProperty().get();

        if (Input.isBlank(serverAddress)) {
            setStatus(LOGIN_BLANK_SERVER_FIELD);
        } else if (Input.isBlank(emailAddress)) {
            setStatus(LOGIN_BLANK_EMAIL_FIELD);
        } else if (!Emails.isWellFormed(emailAddress)) {
            setStatus(MALFORMED_EMAIL_ADDRESS);
        } else {
            Log.info("Successfully logged in as " + emailAddress + " to " + serverAddress);
            this.serverAddress.set(serverAddress);
            this.emailAddress.set(emailAddress);
            setStatus(LOGIN_SUCCESS);
        }
    }

    public ReadOnlyStringProperty emailAddressProperty() {
        return emailAddress.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty serverAddressProperty() {
        return serverAddress.getReadOnlyProperty();
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

}
