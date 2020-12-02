package di.unito.it.prog3.client.controllers.screens;


import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.client.screen.Screen;
import di.unito.it.prog3.libs.forms.Field;
import di.unito.it.prog3.libs.forms.Form;
import di.unito.it.prog3.libs.forms.FormUtils;
import di.unito.it.prog3.libs.forms.impl.IntegerField.BoundedIntegerField;
import di.unito.it.prog3.libs.forms.impl.StringField.EmailField;
import di.unito.it.prog3.libs.forms.impl.StringField.NonBlankStringField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;

import static di.unito.it.prog3.client.controllers.screens.LoginScreenController.LoginForm.*;
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

    private Form<LoginForm> form;

    @Override
    public void setupControl() {
        ValidationSupport validationSupport = new ValidationSupport();
        /*validationSupport.registerValidator(serverField, (Validator<String>) (control, s) -> {
            boolean condition  = s == null || s.isBlank());

            return ValidationResult.fromMessageIf(
                    control,
                    "Please insert a valid server address",
                    Severity.ERROR,
                    condition);
        });*/

        validationSupport.registerValidator(serverField, Validator.createEmptyValidator("Required"));

        form = new Form<>();

        Field<String> server = form.add(SERVER_ADDRESS, new NonBlankStringField(serverField));
        Field<Integer> port  = form.add(SERVER_PORT, new BoundedIntegerField(portField, 1, 65535));
        Field<String> email  = form.add(EMAIL, new EmailField(emailField));

        if (DEBUG) {
            server.initialize(System.getProperty("server"));
            port.initialize(System.getProperty("port"));
            email.initialize(System.getProperty("email"));
        }

        FormUtils.show(serverFieldError).onlyWhen(server.isInvalid());
        FormUtils.show(portFieldError).onlyWhen(port.isInvalid());
        FormUtils.show(emailFieldError).onlyWhen(email.isInvalid());

        form.onSubmit(() -> {
            System.out.println(server.getInput() + " " + port.getInput() + " " + email.getInput());
        });

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

    enum LoginForm {
        SERVER_ADDRESS, SERVER_PORT, EMAIL
    }

}
