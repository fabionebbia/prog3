package di.unito.it.prog3.client.controllers.screens;


import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.client.screen.Screen;
import di.unito.it.prog3.libs.forms.Field;
import di.unito.it.prog3.libs.forms.Form;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

import static di.unito.it.prog3.client.controllers.screens.LoginScreenController.LoginField.*;
import static di.unito.it.prog3.client.model.Status.LOGIN_SUCCESS;
import static di.unito.it.prog3.libs.utils.Debug.DEBUG;

public class LoginScreenController extends Controller {

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

    private Form<LoginField> form;

    @Override
    public void setupControl() {

        form = new Form<>();

        /*Field<String> server = form.registerNonBlankString (SERVER_ADDRESS, serverField);
        Field<Integer> port = form.registerBoundedInteger(SERVER_PORT, portField, 1, 65535);

        server.addErrorComponent(serverFieldError);
        port.addErrorComponent(portFieldError);
        email.addErrorComponent(emailFieldError);

        if (DEBUG) {
            server.initialize(System.getProperty("server"));
            port.initialize(System.getProperty("port"));
            email.initialize(System.getProperty("email"));
        }*/


        form.registerNonBlankString(SERVER_ADDRESS, serverField)
                .setErrorLabel(serverFieldError)
                .initialize(System.getProperty("server"));

        form.registerBoundedInteger(SERVER_PORT, portField, 1, 65535)
                .setErrorLabel(portFieldError)
                .initialize(System.getProperty("port"));

        form.registerEmail(EMAIL, emailField)
                .setErrorLabel(emailFieldError)
                .initialize(System.getProperty("email"));

        form.setSubmitControl(submitButton);

        form.computeFocus();


        submitButton.setOnAction(e -> form.submit());

        Platform.runLater(() -> submitButton.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                form.submit();
            }
        }));

        model.onStatus(LOGIN_SUCCESS, () -> {
            try {
                screenManager.setScreen(Screen.MAIN);
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        });
    }

    enum LoginField {
        SERVER_ADDRESS, SERVER_PORT, EMAIL
    }

}
