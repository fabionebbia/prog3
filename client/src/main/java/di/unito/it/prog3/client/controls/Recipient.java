package di.unito.it.prog3.client.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Recipient extends HBox {

    @FXML
    private Hyperlink emailDisplay;

    @FXML
    private Button removeButton;

    private final StringProperty emailAddress;

    public Recipient(String email) throws IOException {
        FXMLLoader l = new FXMLLoader(getClass().getResource("/controls/recipient.fxml"));
        l.setController(this);
        l.setRoot(this);
        l.load();

        emailAddress = new SimpleStringProperty(email);
        emailDisplay.textProperty().bind(emailAddress);
        removeButton.setOnAction(e -> ((Pane) getParent()).getChildren().remove(this));
    }

}
