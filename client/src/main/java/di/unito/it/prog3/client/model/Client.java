package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseModel;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.Timer;
import java.util.TimerTask;

import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;
import static di.unito.it.prog3.client.model.ClientStatus.IDLE;
import static di.unito.it.prog3.client.model.Status.*;

public class Client extends BaseModel<ClientStatus> {

    private final Timer timer;
    private final Model model;

    /*private final ReadOnlyStringWrapper serverAddress;
    private final ValidableStringProperty serverUrl;
    private final ValidableStringProperty serverPort;
    private final ValidableStringProperty userEmail;*/

    private final ReadOnlyStringWrapper serverAddress;
    private final ReadOnlyStringWrapper userEmail;

    public Client(Model model) {
        super(IDLE);
        this.model = model;
        timer = new Timer();

        /*serverUrl = new LoginFormField(server, Input::isBlank, LOGIN_INVALID_SERVER_ADDRESS);
        serverPort = new LoginFormField(port, Input::isPort, LOGIN_INVALID_SERVER_PORT);
        userEmail = new LoginFormField(email, Input::isEmail, LOGIN_INVALID_EMAIL);

        serverAddress = new ReadOnlyStringWrapper();
        serverAddress.bind(Bindings.concat(serverUrl, ":", serverPort));*/

        serverAddress = new ReadOnlyStringWrapper();
        userEmail = new ReadOnlyStringWrapper();
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

    public void login(String server, int port, String email) {
        System.out.println(email + "@" + server + ":" + port);
        userEmail.set(server + ":" + port);
        model.setStatus(LOGIN_SUCCESS);

        /*
        new ShortCircuitCombinedValidator<>()
            .begin(serverAddress)
                .check(Input::isBlank)
                    .ifTrue(() -> model.setStatus(LOGIN_BLANK_SERVER_ADDRESS))
            .next(serverPort)
                .check(Input::isBlank)
                    .ifTrue(() -> model.setStatus(LOGIN_BLANK_SERVER_PORT))
                .check(Input::isPort)
                    .ifTrue(() -> model.setStatus(LOGIN_MALFORMED_SERVER_PORT))
            .next(userEmail)
                .check(Input::isBlank)
                    .ifTrue(() -> model.setStatus(LOGIN_BLANK_EMAIL))
                .check(Emails::isWellFormed)
                    .ifTrue(() -> model.setStatus(MALFORMED_EMAIL_ADDRESS))
                    .ifFalse(() -> model.setStatus(LOGIN_SUCCESS));
        */
    }

    public ReadOnlyStringProperty serverAddressProperty() {
        return serverAddress.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty userEmailProperty() {
        return userEmail.getReadOnlyProperty();
    }

    public void shutdown() {
        timer.cancel();
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
