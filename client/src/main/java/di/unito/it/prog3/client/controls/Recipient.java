package di.unito.it.prog3.client.controls;

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

    public StringProperty emailProperty() {
        return emailDisplay.textProperty();
    }

    public void onRemove(EventHandler<ActionEvent> eventHandler) {
        removeButton.setOnAction(eventHandler);
    }

    public void onClick(EventHandler<ActionEvent> eventHandler) {
        emailDisplay.setOnAction(eventHandler);
    }

}
