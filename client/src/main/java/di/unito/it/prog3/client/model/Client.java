package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.client.fxml.model.BaseModel;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.store.*;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;
import static di.unito.it.prog3.client.model.ClientStatus.IDLE;
import static di.unito.it.prog3.client.model.OldStatus.*;

public class Client extends BaseModel implements EmailStore {

    private final Timer timer;
    private final Model model;

    private final ReadOnlyStringWrapper serverAddress;
    private final ReadOnlyStringWrapper userEmail;

    private final EmailStore localStore;

    public Client(Model model) {
        super(IDLE);
        this.model = model;

        timer = new Timer();
        userEmail = new ReadOnlyStringWrapper("");
        serverAddress = new ReadOnlyStringWrapper("localhost:1919");

        localStore = new LocalJsonEmailStore("_store");
    }

    public void start() {
        TimerTask blinkTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (getStatus() == CONNECTED)
                        setStatus(IDLE);
                    else setStatus(CONNECTED);
                });
            }
        };
       timer.schedule(blinkTask, 0, 2000);
    }

    private void fireIf(boolean condition, OldStatus status) {
        model.fireIf(condition, status);
    }

    private void fire(OldStatus status) {
        model.fire(status);
    }

/*

    public LoginForm.LoginResponse login(String server, int port, String email) {
        fireIf(server != null && !server.isBlank(), LOGIN_INVALID_SERVER_ADDRESS);

        fireIf(port < 1 || port > 65535, LOGIN_INVALID_SERVER_PORT);

        fireIf(email == null || email.isBlank(),

        if (email == null || email.isBlank()) {
            model.emailInputStatus.set(EmailStatus.BLANK);
        }
        if (Emails.isWellFormed(email)) {
            model.emailInputStatus.set(EmailStatus.MALFORMED);
        }
        if (!userExists(email)) {
            model.emailInputStatus.set(EmailStatus.UNKNOWN);
        }

        if (userExists(email)) {
            model.emailInputStatus.set(EmailStatus.UNKNOWN);
            serverAddress.set(server + ":" + port);
            userEmail.set(email);

            model.setStatus(LOGIN_SUCCESS);
        } else {
            model.setStatus(LOGIN_UNKNOWN_USER);
        }

        if (callback != null) {
            // callback.call(model.getStatus());
            callback.call(model.getStatus());
        }
    }
*/
    public ReadOnlyStringProperty serverAddressProperty() {
        return serverAddress.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty userEmailProperty() {
        return userEmail.getReadOnlyProperty();
    }

    public void shutdown() {
        timer.cancel();
    }

    @Override
    public boolean userExists(String userMail) {
        return localStore.userExists(userMail);
    }

    @Override
    public void store(Email email) throws EmailStoreException {

    }

    @Override
    public void delete(Email.ID email) throws EmailStoreException {

    }

    @Override
    public Email read(Email.ID email) throws EmailStoreException, FileNotFoundException {
        return null;
    }

    @Override
    public List<Email> read(Email.ID offset, int many) throws EmailStoreException {
        return null;
    }

    @Override
    public List<Email> readAll(Queue queue) throws EmailStoreException {
        return null;
    }

    /*
    private class LoginFormField extends ValidableStringProperty {
        private final Status statusToBeSetOnInvalid;

        LoginFormField(String initValue, Validator<String> validator, Status statusToBeSetOnInvalid) {
            super(initValue, validator);
            this.statusToBeSetOnInvalid = statusToBeSetOnInvalid;
        }

        @Override
        public void onInvalid(String value) {
            model.setStatus(statusToBeSetOnInvalid);
        }
    }*/

}
