package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

public class ReadController extends Controller {

    @FXML private FlowPane recipientsFlowPane;
    @FXML private Label dateLabel;
    @FXML private Label subjectLabel;
    @FXML private Label fromLabel;
    @FXML private TextArea bodyTextArea;


    @Override // Nothing to do here
    public void setupControl() {}


    /**
     * Called by controllers in the same package to set displayed e-mail.
     *
     * @param email The e-mail that must be displayed.
     */
    void showEmail(Email email) {
        fromLabel.setText(email.getSender());
        subjectLabel.setText(email.getSubject());
        bodyTextArea.setText(email.getBody());
        dateLabel.setText(Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(email.getTimestamp()));

        recipientsFlowPane.getChildren().clear();
        for (String recipient : email.getRecipients()) {
            Label recipientLabel = new Label(recipient.toLowerCase());
            recipientLabel.getStyleClass().add("read-only-recipient");
            recipientsFlowPane.getChildren().add(recipientLabel);
        }
    }
}
