package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.screen.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

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
