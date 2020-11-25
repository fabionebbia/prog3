package di.unito.it.prog3.client.controllers;


import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.client.views.ClientScreen;
import di.unito.it.prog3.client.fxml.BaseController;
import di.unito.it.prog3.client.fxml.ScreenManager;
import di.unito.it.prog3.libs.utils.Input;
import di.unito.it.prog3.libs.utils.Log;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static di.unito.it.prog3.client.model.Status.LOGIN_SUCCESS;

public class LoginController extends BaseController<Model> {

    @FXML
    private TextField serverField;

    @FXML
    private TextField emailField;

    @FXML
    private Button submitButton;

    @FXML
    private Label messageLabel;

    @Override
    public void init(ScreenManager screenManager, Model model) {
        super.init(screenManager, model);

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
        model.onStatus(LOGIN_SUCCESS, this::changeScreen);

        Platform.runLater(() -> {
            if (Input.isBlank(model.serverAddressProperty())) {
                serverField.requestFocus();
            } else if (Input.isBlank(model.emailAddressProperty())) {
                emailField.requestFocus();
            } else {
                submitButton.requestFocus();
            }
        });

        /* On successful login, change screen to the e-mail browse screen
        model.statusProperty().addListener(((observableStatus, oldStatus, newStatus) -> {
            if (newStatus == LOGIN_SUCCESS) {
                try {
                    screenManager.loadAndSet(ClientScreen.BROWSE, model);
                } catch (IOException e) {
                    // TODO handle fatal error: show alert box with an option to close the app
                    Log.error("Could not set browse screen");
                    e.printStackTrace();
                }
            }
        }));*/
    }

    // Asks the model to perform login
    private void submit() {
        // submitButton.requestFocus();
        model.login();
    }

    private void changeScreen() {
        try {
            screenManager.loadAndSet(ClientScreen.TEST, model);
        } catch (IOException e) {
            // TODO handle fatal error: show alert box with an option to close the app
            Log.error("Could not set browse screen");
            e.printStackTrace();
        }
    }
}
