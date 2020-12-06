package di.unito.it.prog3.client.controllers.screens;


import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.client.model.LoginRequest;
import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.libs.forms.v2.FormManager2;
import di.unito.it.prog3.libs.forms.v2.SubmitHandler;
import di.unito.it.prog3.libs.model.Error;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginScreenController extends Controller implements SubmitHandler<LoginForm> {

    @FXML
    private TextField serverField;

    @FXML
    private TextField portField;

    @FXML
    private TextField emailField;

    @FXML
    private Label serverFieldError;

    @FXML
    private Label portFieldError;

    @FXML
    private Label emailFieldError;

    @FXML
    private Button submitButton;

    private FormManager2<LoginForm> form;

    private final StringProperty serverErrorMessage = new SimpleStringProperty();
    private final StringProperty portErrorMessage = new SimpleStringProperty();
    private final StringProperty emailErrorMessage = new SimpleStringProperty();

    @Override
    protected void setupControl() {

        // force only 1-65535
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            } else if (!newValue.isBlank()) {
                int portValue = Integer.parseInt(newValue);
                if (portValue < 1 || portValue > 65535) {
                    portErrorMessage.set("Should be between 1-65535");
                } else {
                    model.serverPortProperty().set(portValue);
                    portErrorMessage.set("");
                }
            }
        });

        serverField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (wasFocused) {
                String text =  serverField.getText();
                if (text == null || text.isBlank()) {
                    serverErrorMessage.set("Cannot be blank");
                }
            }
            if (isFocused && !serverErrorMessage.isEmpty().get()) {
                serverErrorMessage.set("");
            }
        });

        model.serverAddressProperty().bindBidirectional(serverField.textProperty());
        model.loggedUserMailProperty().bindBidirectional(emailField.textProperty());

        serverFieldError.textProperty().bind(serverErrorMessage);
        portFieldError.textProperty().bind(portErrorMessage);
        emailFieldError.textProperty().bind(emailErrorMessage);


        /*form = new FormManager2<>(LoginForm.class, this);

        FormField2 server = form.register(SERVER, serverField);
        FormField2 port = form.register(PORT, portField);
        FormField2 email = form.register(EMAIL, emailField);

        server.attachErrorLabel(serverFieldError);
        port.attachErrorLabel(portFieldError);
        email.attachErrorLabel(emailFieldError);

        form.setSubmitControl(submitButton);

        form.computeFocus();*/

        /*model.serverAddressProperty().bind(serverField.textProperty());
        model.serverPortProperty().bind(Bindings.createIntegerBinding(
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return Integer.parseInt(portField.getText());
                    }
                }
        ), portField.textProperty());
        model.userEmailProperty().bind(emailField.textProperty());*/


        /*form2 = new FormManager2<>(LoginForm.class, model);

        server2 = form2.register(SERVER, serverField);
        port2 = form2.register(PORT, portField);
        email2 = form2.register(EMAIL, emailField);

        server2.attachErrorLabel(serverFieldError);
        port2.attachErrorLabel(portFieldError);
        email2.attachErrorLabel(emailFieldError);

        server2.initialize(System.getProperty("server"));
        port2.initialize(System.getProperty("port"));
        email2.initialize(System.getProperty("email"));

        form2.setSubmitControl(submitButton);
        form2.computeFocus();*/

/*
        CssUtils.ensureClassSet(serverFieldError, "error-label");
        CssUtils.ensureClassSetOnlyIf(serverFieldError, "error-occurred", Bindings.createBooleanBinding(
                () -> model.serverInputStatusProperty().get().isError(), model.serverInputStatusProperty()
        ));

        emailFieldError.textProperty().bind(Bindings.createStringBinding(
                () -> model.serverInputStatusProperty().get().getMessage(), model.serverInputStatusProperty()
        ));


        CssUtils.ensureClassSet(emailFieldError, "error-label");
        CssUtils.ensureClassSetOnlyIf(emailFieldError, "error-occurred", Bindings.createBooleanBinding(
                () -> model.emailInputStatusProperty().get().isError(), model.emailInputStatusProperty()
        ));

        emailFieldError.textProperty().bind(Bindings.createStringBinding(
                () -> model.emailInputStatusProperty().get().getMessage(), model.emailInputStatusProperty()
        ));


*/

        /*model.onStatus(LOGIN_SUCCESS, () -> screenManager.setScreen(Screen.MAIN));
        model.onStatus(LOGIN_UNEXISTING_USER, () -> email2.setErrorMessage(""));


        form = new FormManager<>(LoginForm.class);

        FormField<String> server = form.registerNonBlankString(SERVER, serverField);
        FormField<Integer> port = form.registerBoundedInteger(PORT, portField, 1, 65535);
        FormField<String> email = form.registerEmail(EMAIL, emailField);

        server.attachErrorLabel(serverFieldError);
        port.attachErrorLabel(portFieldError);
        email.attachErrorLabel(emailFieldError);

        server.initialize(System.getProperty("server"));
        port.initialize(System.getProperty("port"));
        email.initialize(System.getProperty("email"));

        form.setSubmitControl(submitButton);
        form.onSubmit(this::handleSubit);
        form.computeFocus();

        /*Platform.runLater(() -> submitButton.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submit();
            }
        }));

        model.onStatus(LOGIN_SUCCESS, () -> screenManager.setScreen(Screen.MAIN));

        model.onStatus(LOGIN_UNEXISTING_USER, () ->
                email.setErrorMessage()
        );*/
    }

    public void submit(Event e) {
        if (e instanceof KeyEvent) {
            if (((KeyEvent) e).getCode() != KeyCode.ENTER) {
               return;
            }
        }

        /*model.login(response -> {
            if (response.isError()) {
                screenManager.displayError(response.toError());
            }
        });*/

        int i = 1;






        model.login(this::handle);




        Perform.async(model::login, this::handle);




        Perform.async(i, this::magic, this::handleMagic);

        // Perform.async(form, this::submit, this::handleResponse);



        Perform.async(() -> this.magic(i), this::handleMagic);










        Perform.async(model::login, response -> {
            if (response.isError()) {
                screenManager.displayError(response.toError());
            }
        });












        /*form.submit(loginForm -> {
            System.out.println("submitted");
            model.login(response -> {
                if (response.isError()) {
                    screenManager.displayError(
                            new Error("Login error", "Cannot log in", response.getMessage())
                    );
                }
                System.out.println("called back 2");
            });
        });*/

        /*String server = serverField.getText();
        String email = emailField.getText();
        try {
            int port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException ignored) {

        }

        LoginRequest request = new LoginRequest(server, port, email);

        CompletableFuture.supplyAsync(() -> model.login(server, port, email))
                .thenAccept(this::handleResponse)
                .exceptionally(this::errorHandler);*/
    }

    private String magic(int i) {
        return String.valueOf(i * 2);
    }

    private void handleMagic(String magic) {
        System.out.println("Tadaa: " +magic);
    }

    private void handle(LoginRequest.LoginResponse response) {

    }

    @Override
    public void submit(LoginForm committedForm) {

    }
}
