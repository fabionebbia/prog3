package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.controllers.LoginFormController.ResponseListener;
import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.client.fxml.model.BaseModel;
import di.unito.it.prog3.client.fxml.model.BaseStatus;
import di.unito.it.prog3.client.fxml.model.EmailProperty;
import di.unito.it.prog3.client.model.LoginRequest.LoginResponse;
import di.unito.it.prog3.client.model.requests.ResponseCallback;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.forms.v2.FormManager2;
import di.unito.it.prog3.libs.model.*;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.beans.property.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static di.unito.it.prog3.client.model.OldStatus.*;

public class Model extends BaseModel {

    // Login
    private final ConstrainedStringProperty serverAddress;
    private final ConstrainedIntegerProperty serverPort;
    private final ConstrainedStringProperty loggedUserMail;
    private final ReadOnlyBooleanWrapper loggedIn;

    final ReadOnlyObjectWrapper<EmailStatus> emailInputStatus;
    final ReadOnlyObjectWrapper<ServerInputStatus> serverInputStatus;

    private final ListProperty<Email> emails;
    private final EmailProperty currentEmail;
    private final Client client;

    private final ExecutorService executor;

    public Model() {
        super(IDLE);

        client = new Client(this);
        serverInputStatus = new ReadOnlyObjectWrapper<>(ServerInputStatus.BLANK);
        emailInputStatus = new ReadOnlyObjectWrapper<>(EmailStatus.BLANK);

        currentEmail = new EmailProperty();
        emails = new SimpleListProperty<>();

        // Login
        serverAddress = new NonBlankStringProperty();
        serverPort = new BoundedIntegerProperty(1, 65535);

        loggedIn = new ReadOnlyBooleanWrapper();

        String initEmail = System.getProperty("email");
        loggedUserMail = new EmailStringProperty();


        executor = Executors.newSingleThreadExecutor();
        Perform.setExecutor(executor);
    }

    public ReadOnlyObjectProperty<BaseStatus> clientStatusProperty() {
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

    public void stop() {
        client.shutdown();
        executor.shutdown();
    }

    public ReadOnlyObjectProperty<ServerInputStatus> serverInputStatusProperty() {
        return serverInputStatus.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<EmailStatus> emailInputStatusProperty() {
        return emailInputStatus.getReadOnlyProperty();
    }

    public void fireIf(boolean condition, OldStatus status) {
        if (condition) setStatus(status);
    }

    public void fire(OldStatus status) {
        fireIf(true, status);
    }

    public FormManager2<LoginForm> newLoginForm() {
        /*FormManager2<LoginForm> form = new FormManager2<>(LoginForm.class, this);
        form.initialize(LoginForm.Field.SERVER, System.getProperty("server"));
        form.initialize(LoginForm.Field.PORT, System.getProperty("port"));
        form.initialize(LoginForm.Field.EMAIL, System.getProperty("email"));
        return form;*/ return null;
    }

    public ReadOnlyBooleanProperty loggedIn() {
        return loggedIn;
    }

    /*public ResponseListener login() {
        return new ResponseListener(() -> {

            String email = userEmail.get();
            if (email != null && Emails.isWellFormed(email)) {
                userEmail.addListener((observable, oldValue, newValue) -> {
                    throw new IllegalStateException("User e-mail modified while logged in");
                });
                userEmail.unbind();
                return LOGIN_SUCCESS;
            } else {
                userEmail.set("");
                return LOGIN_FAILURE;
            }
        });
    }*/

    /*public ResponseListener<OldStatus> log1n() { return new ResponseListener<>(() -> {
            String email = userEmail.get();
            if (email != null && Emails.isWellFormed(email)) {
                userEmail.addListener((observable, oldValue, newValue) -> {
                    throw new IllegalStateException("User e-mail modified while logged in");
                });
                userEmail.unbind();
                return LOGIN_SUCCESS;
            } else {
                userEmail.set("");
                return LOGIN_FAILURE;
            }
        });
    }*/

    public LoginResponse login() {
        if (loggedIn.get()) {
            return LoginResponse.ALREADY_LOGGED_IN;
        }

        String user = loggedUserMail.get();
        if (user == null || user.isBlank()) {
            return LoginResponse.BLANK_EMAIL;
        }

        if (!Emails.isWellFormed(user)) {
            return LoginResponse.MALFORMED_EMAIL;
        }

        if (!client.userExists(user)) {
            return LoginResponse.UNKNOWN_EMAIL;
        }

        loggedUserMail.set(user);
        loggedIn.set(true);

        return LoginResponse.SUCCESS;
    }

    public void login(ResponseCallback<LoginResponse> callback) {
        executor.submit(() -> {
            if (loggedIn.get()) {
                callback.call(LoginResponse.ALREADY_LOGGED_IN);
                return;
            }

            String user = loggedUserMail.get();
            if (user == null || user.isBlank()) {
                setStatus(LoginResponse.BLANK_EMAIL);
                callback.call(LoginResponse.BLANK_EMAIL);
            }

            if (!Emails.isWellFormed(user)) {
                callback.call(LoginResponse.MALFORMED_EMAIL);
                setStatus(LoginResponse.MALFORMED_EMAIL);
            }

            if (!client.userExists(user)) {
                callback.call(LoginResponse.UNKNOWN_EMAIL);
                setStatus(LoginResponse.UNKNOWN_EMAIL);
            }

            loggedUserMail.set(user);
            loggedIn.set(true);

            callback.call(LoginResponse.SUCCESS);
            setStatus(LoginResponse.SUCCESS);
        });
    }

    public ConstrainedStringProperty serverAddressProperty() {
        return serverAddress;
    }

    public ConstrainedIntegerProperty serverPortProperty() {
        return serverPort;
    }

    public ConstrainedStringProperty loggedUserMailProperty() {
        return loggedUserMail;
    }
}
