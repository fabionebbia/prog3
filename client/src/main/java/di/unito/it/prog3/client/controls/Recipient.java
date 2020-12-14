package di.unito.it.prog3.client.controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;

import java.io.IOException;

// Oracle - Creating a Custom Control with FXML
// url: https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm
public class Recipient extends HBox {

    @FXML @SuppressWarnings("unused")
    private Hyperlink emailDisplay;

    @FXML @SuppressWarnings("unused")
    private Button removeButton;

    public Recipient() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls/recipient.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();
    }

    public void setEmail(String email) {
        emailDisplay.setText(email);
    }

    public void onRemoveButtonPressed(EventHandler<ActionEvent> eventHandler) {
        removeButton.setOnAction(eventHandler);
    }

    public void onClick(EventHandler<ActionEvent> eventHandler) {
        emailDisplay.setOnAction(eventHandler);
    }

    public void setReadOnly(boolean readOnly) {
        removeButton.setVisible(!readOnly);
        removeButton.setManaged(!readOnly);
        removeButton.setDisable(readOnly);

        emailDisplay.setDisable(!readOnly);
    }

}
