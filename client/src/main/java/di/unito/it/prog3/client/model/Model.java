package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseModel;
import di.unito.it.prog3.client.fxml.model.EmailProperty;
import di.unito.it.prog3.libs.pojos.Email;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.Input;
import di.unito.it.prog3.libs.utils.Log;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.Date;

import static di.unito.it.prog3.client.model.Status.*;

public class Model extends BaseModel<Status> {

    private final ReadOnlyStringWrapper serverAddress;
    private final ReadOnlyStringWrapper emailAddress;
    private final EmailProperty currentEmail;
    private final Client client;

    public Model(Client client, String server, String username) {
        super(IDLE);
        this.client = client;
        serverAddress = new ReadOnlyStringWrapper(server);
        emailAddress = new ReadOnlyStringWrapper(username);

        // TODO tmp
        Email e = new Email.EmailBuilder("a@b.c/R/11375")
                .setSender("fabiana.vernero@unito.it")
                .addRecipient("fabionebbia@edu.unito.it")
                .addRecipient("lacrimucce@tante.it")
                .setSentDate(new Date())
                .setSubject("Fragole e asparagi")
                .setBody("Buongiorno!\nE benvenuti alla terza lezione..")
                .build();
        currentEmail = new EmailProperty(e);
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

    public EmailProperty currentEmailProperty() {
        return currentEmail;
    }

}
