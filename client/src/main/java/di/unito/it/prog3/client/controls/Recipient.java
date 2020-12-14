package di.unito.it.prog3.client.controls;

import di.unito.it.prog3.libs.utils.Utils;
import javafx.beans.property.StringProperty;
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

    @FXML
    private Hyperlink emailDisplay;

    @FXML
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
        // removeButton.visibleProperty().set(!readOnly);
        // removeButton.managedProperty().set(!readOnly);
        // removeButton.disableProperty().set(readOnly);

        removeButton.setVisible(!readOnly);
        removeButton.setManaged(!readOnly);
        removeButton.setDisable(readOnly);

        // emailDisplay.disableProperty().set(!readOnly);

        emailDisplay.setDisable(!readOnly);
    }

}
