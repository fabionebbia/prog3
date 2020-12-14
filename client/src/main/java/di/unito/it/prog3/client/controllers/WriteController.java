package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.Recipient;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class WriteController extends Controller {

    // JavaFX controls
    @FXML private GridPane recipientsFlowPaneContainer;
    @FXML private TextField recipientField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyTextArea;
    @FXML private GridPane gridPane;
    @FXML private FlowPane flowPane;
    @FXML private Button addButton;

    // Local "sandbox" properties used as a proxy to avoid
    // contaminating the model with potentially invalid user inputs
    private final SetProperty<String> recipients;
    private final StringProperty subject;
    private final StringProperty body;

    // Binding that specify whether the recipient the user is
    // typing in the recipient Text Field can or cannot be added
    // to the committed e-mail recipients
    private BooleanBinding canAddRecipient;


    // Just initializes local properties
    public WriteController() {
        recipients = new SimpleSetProperty<>(FXCollections.observableSet());
        subject = new SimpleStringProperty();
        body = new SimpleStringProperty();
    }


    /**
     * Called on write view first load.
     * Sets up all write view controls and sets some listeners.
     */
    @Override
    protected void setupControl() {
        // Bind controls text properties to local properties
        subjectField.textProperty().bindBidirectional(subject);
        bodyTextArea.textProperty().bindBidirectional(body);

        // Updates flow pane recipients whenever the recipients set changes
        // i.e. when the user adds a new recipient or removes a previous one
        recipients.addListener((SetChangeListener<String>) change -> {

            // Clear previously added recipients
            flowPane.getChildren().clear();

            // Iterate over current recipients set
            // and update the view to reflect the current status
            for (String recipient : recipients) {
                try {
                    // Create a new Recipient custom control
                    Recipient node = new Recipient();

                    // Set Recipient's displayed e-mail to current recipient
                    node.setEmail(recipient);

                    // Whenever the user presses the Recipient's remove button,
                    // reflect the recipient removal in the recipients set
                    node.onRemoveButtonPressed(e -> recipients.remove(recipient));

                    // Whenever the user clicks on the Recipient's displayed e-mail,
                    // remove the associated recipient from the recipients set
                    // and show it in the recipient TextField to allow modification
                    node.onClick(e -> {
                        recipients.remove(recipient);
                        recipientField.textProperty().set(recipient);
                    });

                    // Add the newly crated Recipient in the recipients FlowPane
                    flowPane.getChildren().add(node);
                } catch (IOException e) {
                    throw new RuntimeException("Could not load graphic components", e);
                }
            }
        });

        // Show recipients FlowPane
        Utils.bindVisibility(recipients.sizeProperty().greaterThan(0), flowPane);

        canAddRecipient = Bindings.createBooleanBinding(() -> {
            String newRecipient = recipientField.textProperty().get();
            return Emails.isWellFormed(newRecipient) && !recipients.contains(newRecipient);
        }, recipientField.textProperty());

        addButton.disableProperty().bind(canAddRecipient.not());

        recipientsFlowPaneContainer.vgapProperty().bind(
                Bindings.createDoubleBinding(() -> recipients.size() > 0 ? 10d : 0d, recipients)
        );
    }

    @Override
    void onDisplayed() {
        gridPane.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && canAddRecipient.get()) {
                addRecipient();
            }
        });
    }

    @Override
    void onHidden() {
        gridPane.getScene().setOnKeyPressed(null);
    }

    @FXML
    private void addRecipient() {
        String newRecipient = recipientField.getText();
        recipients.add(newRecipient);
        recipientField.setText("");
    }

    void open(WriteMode mode) {
        switch (mode) {
            case NEW -> {
                recipientField.requestFocus();
                recipients.clear();
                subject.set("");
                body.set("");
            }
            case FORWARD -> openForward();
            case REPLY -> openReply(false);
            case REPLY_ALL -> openReply(true);
        }
    }

    private void openForward() {
        Email currentEmail = model.openCurrentEmail();

        recipients.clear();

        String newBody = "----------- Forwarded e-mail -----------"
                       + "\nSubject: " + currentEmail.getSubject()
                       + "\nFrom:    " + currentEmail.getSender()
                       + "\nTo:      " + Utils.join(currentEmail.getRecipients(), ", ")
                       + "\nDate:    " + Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(currentEmail.getTimestamp())
                       + "\n\n--------------- Message ----------------"
                       + "\n" + currentEmail.getBody();

        String newSubject = computeNewSubject("Fwd", currentEmail);

        subject.set(newSubject);
        body.set(newBody);
    }

    private void openReply(boolean all) {
        Email currentEmail = model.openCurrentEmail();

        recipients.clear();
        recipients.add(currentEmail.getSender());
        if (all) {
            recipients.addAll(currentEmail.getRecipients());
            if (recipients.size() > 1) {
                recipients.remove(model.getClient().userProperty().get()); // TODO togliere o tenere?
            }
        }

        String newBody = "\n\n------------ Previous e-mail -----------\n"
                       + currentEmail.getSender() + " wrote \"" + currentEmail.getSubject()
                       + "\"\non " + Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(currentEmail.getTimestamp())
                       + "\n\n\n" + currentEmail.getBody();

        String newSubject = computeNewSubject("Re", currentEmail);

        body.set(newBody);
        subject.set(newSubject);

        bodyTextArea.requestFocus();
    }

    void sendRequested(Callback callback) {
        Email email = new Email();
        email.addAllRecipients(recipients.get());
        email.setSubject(subject.get());
        email.setBody(body.get());
        model.send(email, callback);
    }

    BooleanBinding isEmailWellFormed() {
        return recipients.sizeProperty().greaterThan(0);
    }

    private String computeNewSubject(String prefix, Email email) {
        String newSubject = "";
        String prevSubject = email.getSubject();
        if (prevSubject != null && !prevSubject.isBlank()) {
            newSubject = prefix + ": " + prevSubject;
        }
        return newSubject;
    }

}
