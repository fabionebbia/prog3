package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.Recipient;
import di.unito.it.prog3.client.controls.RecipientsFlowPane;
import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.libs.utils.Utils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import di.unito.it.prog3.libs.model.Error;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadController extends Controller {


    @FXML
    private FlowPane recipientsFlowPane;

    @FXML
    private Label dateLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label fromLabel;

    @FXML
    private TextArea bodyTextArea;

    @Override
    public void setupControl() {
        model.currentEmailProperty().recipientsProperty().addListener((observable, oldValue, newValue) -> {
            recipientsFlowPane.getChildren().clear();
            /*for (String recipient : newValue) {
                recipientsFlowPane.getChildren().add(new Label(recipient));
            }*/
            for (int i = 0; i < newValue.size(); i++) {
                System.out.println(newValue.get(i));
                Label recipientLabel = new Label(
                        newValue.get(i) + ((i < newValue.size() - 1) ? "," : "")
                );
                recipientsFlowPane.getChildren().add(recipientLabel);
            }
        });

        fromLabel.textProperty().bind(model.currentEmailProperty().fromProperty());
        subjectLabel.textProperty().bind(model.currentEmailProperty().subjectProperty());
        bodyTextArea.textProperty().bind(model.currentEmailProperty().bodyProperty());

    }

}
