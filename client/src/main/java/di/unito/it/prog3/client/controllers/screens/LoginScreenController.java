package di.unito.it.prog3.client.controllers;


import di.unito.it.prog3.client.engine.Controller;
import di.unito.it.prog3.client.views.Screens;
import di.unito.it.prog3.libs.utils.Input;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

import static di.unito.it.prog3.client.model.Status.LOGIN_SUCCESS;

public class LoginController extends Controller {

    @FXML
    private TextField serverField;

    @FXML
    private TextField emailField;

    @FXML
    private Button submitButton;

    @FXML
    private Label messageLabel;

    @Override
    public void setupControl() {
        // Submit login form on submit button press
        submitButton.setOnAction(e -> submit());

        serverField.textProperty().bind(model.serverAddressProperty());

        emailField.textProperty().bind(model.emailAddressProperty());

        // Submit login form on ENTER key press
        // Executed in a `runLater` to allow scene to be populated
        Platform.runLater(() -> submitButton.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                submit();
            }
        }));

        // Bind UI status message label to model's status message property
        messageLabel.textProperty().bind(model.statusMessageProperty());

        // On successful login, change screen to the e-mail browse screen
        model.onStatus(LOGIN_SUCCESS, () -> {
            try {
                //screenManager.setScreen(BrowseScreen.class);
                screenManager.setScreen(Screens.MAIN);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> {
            if (Input.isBlank(model.serverAddressProperty())) {
                serverField.requestFocus();
            } else if (Input.isBlank(model.emailAddressProperty())) {
                emailField.requestFocus();
            } else {
                submitButton.requestFocus();
            }
        });
    }

    // Asks the model to perform login
    private void submit() {
        submitButton.requestFocus();
        model.login();
    }

}
