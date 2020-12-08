package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.communication.net.responses.Response;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.screen.SuperController;
import di.unito.it.prog3.libs.communication.ResponseHandler;
import di.unito.it.prog3.libs.forms.v2.CommitHandler;
import di.unito.it.prog3.libs.forms.v2.FormField2;
import di.unito.it.prog3.libs.forms.v2.FormManager2;
import di.unito.it.prog3.libs.model.Error;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;

public class LoginScreenController extends SuperController<Model>
                                   implements CommitHandler<LoginForm>, ResponseHandler<Response> {

    private FormManager2<LoginForm> form;

    @FXML private TextField serverField;
    @FXML private TextField portField;
    @FXML private TextField emailField;

    @FXML private Label serverFieldError;
    @FXML private Label portFieldError;
    @FXML private Label emailFieldError;

    @FXML private Button submitButton;


    @Override
    protected void setupControl() {
        form = new FormManager2<>(LoginForm.class, this);
        FormField2 server = form.register(LoginForm.Field.SERVER, serverField);
        FormField2 port = form.register(LoginForm.Field.PORT, portField);
        FormField2 email = form.register(LoginForm.Field.EMAIL, emailField);

        if (Boolean.parseBoolean(System.getProperty("debug"))) {
            server.set(System.getProperty("server"));
            port.set(System.getProperty("port"));
            email.set(System.getProperty("email"));
        }

        server.attachErrorLabel(serverFieldError);
        port.attachErrorLabel(portFieldError);
        email.attachErrorLabel(emailFieldError);

        // force integers only on port TextField
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith("0")) {
                portField.setText(newValue.substring(1));
            } else if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        form.setSubmitControl(submitButton);
        form.computeFocus();
    }

    public void submit(Event e) {
        if (e instanceof KeyEvent && ((KeyEvent) e).getCode() != KeyCode.ENTER) {
            return;
        }
        form.commit();
    }

    @Override
    public void committed(LoginForm lf) {
        String server = lf.getServer();
        int port = lf.getPort();
        String user = lf.getEmail();

        Perform.async(() -> model.login(server, port, user), this);
    }

    @Override
    public void onResponse(Response response) {
        if (response.success()) {
            screenManager.loadScene("main", parent -> {
                Rectangle2D computerScreen = Screen.getPrimary().getBounds();
                double width = computerScreen.getWidth() / 3;
                double height = computerScreen.getHeight();
                return new Scene(parent, width, height);
            });
        } else {
            onError(new Exception("Unknown error"));
        }
    }

    @Override
    public void onError(Throwable e) {
        screenManager.displayError(new Error("Login error", "Could not login", e.getMessage()));
    }

}
