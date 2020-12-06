package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.client.screen.Controller;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LoginFormController extends Controller {

    @FXML
    private TextField userEmailField;

    @FXML
    private Label statusMessageLabel;

    @FXML
    private Button submitButton;

    private final BooleanProperty submitted;
    private final ReadOnlyStringWrapper loginMessage;

    public LoginFormController() {
        submitted = new SimpleBooleanProperty();
        loginMessage = new ReadOnlyStringWrapper("");
    }

    @Override
    protected void setupControl() {

        /*userEmailField.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                userEmailField.textProperty().removeListener(this);
            }
        });*/

        statusMessageLabel.textProperty().bind(loginMessage);
        /*statusMessageLabel.visibleProperty().bind(
                Bindings.createBooleanBinding(
                    () -> submitted.get() && !model.loggedIn().get(), submitted, model.loggedIn()
                )
        );*/

        //model.userEmailProperty().bindBidirectional(userEmailField.textProperty());
        //model.userEmailProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        /*screen.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submit();
            }
        });*/

        /*Platform.runLater(() -> userEmailField.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submit();
            }
        }));*/
    }

    public void submit(LoginForm form) {
        System.out.println("Good");
    }

    public void submit() {
        submitted.set(true);
        submitButton.requestFocus();

        model.login(response -> {
            loginMessage.set(response.getMessage());
            switch (response) {
                case SUCCESS:
                    //screen.setView("/email-list-view.fxml");
                    break;
                case BLANK_EMAIL:
                case MALFORMED_EMAIL:
                    Platform.runLater(() -> userEmailField.requestFocus());
                default: System.out.println(response);
            }
        });

        /*Perform.async(model::login)
                .on(SUCCESS, this::onSuccess)
                .on(INVALID_EMAIL, userEmailField::requestFocus);
        
        Async(model::login)
                .on(SUCCESS, this::onSuccess)
                .on(INVALID_EMAIL, userEmailField::requestFocus);

        model.log1n()
                .on(SUCCESS, this::onSuccess)
                .on(INVALID_EMAIL, userEmailField::requestFocus)
                .async();

        Perform(model::login)
                .on(SUCCESS, this::onSuccess)
                .on(INVALID_EMAIL, userEmailField::requestFocus)
                .async();*/

        /* model.login()
                .on(LOGIN_SUCCESS, this::onSuccess)
                .on(LOGIN_INVALID_EMAIL, userEmailField::requestFocus)
             .perform(); */
    }

    private static class Perform {
        public static <T> ResponseListener<T> async(Supplier<T> request) {
            return new ResponseListener<>(request);
        }
    }

    private static <T> ResponseListener<T> Async(Supplier<T> task) {
        return new ResponseListener<>(task);
    }

    private static <T> ResponseListener<T> Perform(Supplier<T> task) {
        return new ResponseListener<>(task);
    }

    public static class ResponseListener<T> {

        private final Map<T, Callback> callbacks;
        private final Supplier<T> request;

        public ResponseListener(Supplier<T> request) {
            this.request = request;
            callbacks = new HashMap<>();
        }

        public ResponseListener<T> on(T status, Callback callback) {
            if (callbacks.containsKey(status)) {
                throw new RuntimeException(); // TODO
            }
            callbacks.put(status, callback);
            return this;
        }

        public void perform() {
            T result = request.get();
            if (!callbacks.containsKey(result)) {
                throw new RuntimeException(); // TODO
            }
            callbacks.get(result).call();
        }

        public void async() {}

    }

}
