package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.Recipient;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.time.LocalDateTime;

public class ReadController extends Controller {

    @FXML private FlowPane recipientsFlowPane;
    @FXML private Label dateLabel;
    @FXML private Label subjectLabel;
    @FXML private Label fromLabel;
    @FXML private TextArea bodyTextArea;


    @Override
    public void setupControl() {
        /*model.currentEmailProperty().recipientsProperty().addListener((observable, oldValue, newValue) -> {
            recipientsFlowPane.getChildren().clear();
            for (int i = 0; i < newValue.size(); i++) {
                Label recipientLabel = new Label(
                        newValue.get(i) + ((i < newValue.size() - 1) ? "," : "")
                );
                recipientsFlowPane.getChildren().add(recipientLabel);
            }
        });

        fromLabel.textProperty().bind(model.currentEmailProperty().fromProperty());
        subjectLabel.textProperty().bind(model.currentEmailProperty().subjectProperty());
        bodyTextArea.textProperty().bind(model.currentEmailProperty().bodyProperty());

        dateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            LocalDateTime timestamp = model.currentEmailProperty().timestampProperty().get();
            if (timestamp == null) return "";
            else return Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(timestamp);
        }, model.currentEmailProperty().timestampProperty()));*/
    }

    @Override
    void onDisplayed() {
        /*model.currentEmailProperty().recipientsProperty().addListener((observable, oldRecipients, newRecipients) -> {
            recipientsFlowPane.getChildren().clear();
            for (String recipient : newRecipients) {
                try {
                    Recipient node = new Recipient();
                    node.setEmail(recipient);
                    node.setReadOnly(true);
                    /*node.onRemoveButtonPressed(e -> newRecipients.remove(recipient));
                    node.onClick(e -> {
                        newRecipients.remove(recipient);
                        .textProperty().set(recipient);
                    });
                    Label node = new Label(recipient);
                    node.setStyle("-fx-background-color: #fafafa");
                    node.setStyle("-fx-background-radius: 10px");
                    recipientsFlowPane.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fromLabel.textProperty().bind(model.currentEmailProperty().fromProperty());
        subjectLabel.textProperty().bind(model.currentEmailProperty().subjectProperty());
        bodyTextArea.textProperty().bind(model.currentEmailProperty().bodyProperty());

        dateLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            LocalDateTime timestamp = model.currentEmailProperty().timestampProperty().get();
            if (timestamp == null) return "";
            else return Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(timestamp);
        }, model.currentEmailProperty().timestampProperty()));*/
    }

    @Override
    void onHidden() {
       /* fromLabel.textProperty().unbind();
        subjectLabel.textProperty().unbind();
        bodyTextArea.textProperty().unbind();*/
    }

    void setEmail(Email email) {
        fromLabel.setText(email.getSender());
        subjectLabel.setText(email.getSubject());
        bodyTextArea.setText(email.getBody());
        dateLabel.setText(Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(email.getTimestamp()));

        //try {
            recipientsFlowPane.getChildren().clear();

            for (String recipient : email.getRecipients()) {
                /*Recipient node = new Recipient();
                node.setEmail(recipient);
                node.setReadOnly(true);
                recipientsFlowPane.getChildren().add(node);*/
                Label recipientLabel = new Label(recipient);
                recipientLabel.getStyleClass().add("read-only-recipient");
                recipientsFlowPane.getChildren().add(recipientLabel);
            }
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
