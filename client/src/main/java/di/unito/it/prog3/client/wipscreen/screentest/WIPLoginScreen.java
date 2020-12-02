package di.unito.it.prog3.client.wipscreen.screentest;

import di.unito.it.prog3.client.wipscreen.WIPScreen;
import di.unito.it.prog3.client.wipscreen.WIPScreenManager;
import di.unito.it.prog3.libs.utils.Input;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;

import static di.unito.it.prog3.client.model.Status.LOGIN_SUCCESS;

public class WIPLoginScreen extends WIPScreen {

    @FXML
    private TextField serverField;

    @FXML
    private TextField emailField;

    @FXML
    private Button submitButton;

    @FXML
    private Label messageLabel;

    @Override
    protected String getTitle() {
        return "TEST";
    }

    @Override
    protected void setupControl() {
        // serverField.textProperty().bind(model.serverAddressProperty());

        // emailField.textProperty().bind(model.emailAddressProperty());

        // Submit login form on submit button press
        submitButton.setOnAction(e -> submit());

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
                screenManager.setScreen(WIPMainScreen.class, model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> {
            /*if (Input.isBlank(model.serverAddressProperty())) {
                serverField.requestFocus();
            } else if (Input.isBlank(model.emailAddressProperty())) {
                emailField.requestFocus();
            } else {
                submitButton.requestFocus();
            }*/
        });
    }

    // Asks the model to perform login
    private void submit() {
        submitButton.requestFocus();
        //model.login();
    }

    @Override
    protected Scene loadScene(WIPScreenManager screenManager) throws IOException {
        Parent parent = screenManager.loadFXML("/login-screen-wip-test.fxml", this);
        return new Scene(parent, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
    }

}
