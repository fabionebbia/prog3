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

        // Hide recipients FlowPane when the opened e-mail no recipients
        Utils.bindVisibility(recipients.sizeProperty().greaterThan(0), flowPane);

        // Allow adding a new recipient only when its e-mail address
        // is well formed and is not already present in the recipients set
        canAddRecipient = Bindings.createBooleanBinding(() -> {
            String newRecipient = recipientField.textProperty().get();
            return Emails.isWellFormed(newRecipient) && !recipients.contains(newRecipient);
        }, recipientField.textProperty());

        // Disable add button until the recipient can be added
        addButton.disableProperty().bind(canAddRecipient.not());

        // Adjust vgap based on recipients size
        recipientsFlowPaneContainer.vgapProperty().bind(
                Bindings.createDoubleBinding(() -> recipients.size() > 0 ? 10d : 0d, recipients)
        );
    }


    /**
     * Called when this view is displayed on parent screen.
     */
    @Override
    void onDisplayed() {
        // Add ENTER key press handler to add the newly inserted recipient
        // in the recipient set if it can be inserted
        gridPane.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && canAddRecipient.get()) {
                addRecipient();
            }
        });
    }


    /**
     * Called when this view is removed from the parent screen.
     */
    @Override
    void onHidden() {
        // Remove key press event handler
        gridPane.getScene().setOnKeyPressed(null);
    }


    /**
     * Adds the recipient to the recipients set
     * and resets the recipient TextField.
     */
    @FXML
    private void addRecipient() {
        String newRecipient = recipientField.getText();
        recipients.add(newRecipient);
        recipientField.setText("");
    }


    /**
     * Called by controllers in the same package to request
     * write view to set up itself in the specified write mode.
     *
     * @param mode The write mode the write view should open in.
     */
    void open(WriteMode mode) {
        switch (mode) {
            case NEW -> {
                // Simply clear anything,
                // allowing the user to write a new e-mail
                recipientField.requestFocus();
                recipients.clear();
                subject.set("");
                body.set("");
            }
            case FORWARD ->
                // Open in forward mode
                openForward();
            case REPLY ->
                // Open in reply mode with the `all` flag set to false
                openReply(false);
            case REPLY_ALL ->
                // Open in reply mode with the `all` flag set to false
                openReply(true);
        }
    }


    /**
     * Open write view in forward mode.
     */
    private void openForward() {
        // Request the model to open the current e-mail
        Email currentEmail = model.openCurrentEmail();

        // Clear recipients set
        recipients.clear();

        // Compute the new e-mail body
        String newBody = "----------- Forwarded e-mail -----------"
                       + "\nSubject: " + currentEmail.getSubject()
                       + "\nFrom:    " + currentEmail.getSender()
                       + "\nTo:      " + Utils.join(currentEmail.getRecipients(), ", ")
                       + "\nDate:    " + Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(currentEmail.getTimestamp())
                       + "\n\n--------------- Message ----------------"
                       + "\n" + currentEmail.getBody();

        // Prepend "Fwd" to the original the subject as the new default subject
        String newSubject = computeNewSubject("Fwd", currentEmail);

        // Set the fields
        subject.set(newSubject);
        body.set(newBody);
    }


    /**
     * Open read view in reply (or reply all) mode.
     *
     * @param all Flag indicating whether this is a reply or replay all operation.
     */
    private void openReply(boolean all) {
        // request the model to open the current e-mail
        Email currentEmail = model.openCurrentEmail();

        // Reinitialize the recipients set with the correct recipients
        recipients.clear();
        recipients.add(currentEmail.getSender());
        if (all) {
            recipients.addAll(currentEmail.getRecipients());
            if (recipients.size() > 1) {
                recipients.remove(model.getClient().userProperty().get()); // TODO togliere o tenere?
            }
        }

        // Compute the new e-mail body
        String newBody = "\n\n------------ Previous e-mail -----------\n"
                       + currentEmail.getSender() + " wrote \"" + currentEmail.getSubject()
                       + "\"\non " + Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(currentEmail.getTimestamp())
                       + "\n\n\n" + currentEmail.getBody();

        // Prepend "Re" to the original the subject as the new default subject
        String newSubject = computeNewSubject("Re", currentEmail);

        // Set fields
        body.set(newBody);
        subject.set(newSubject);

        // Focus body TextArea so that the user can immediately start typing
        bodyTextArea.requestFocus();
    }


    /**
     * Used by controllers in the same package to notify write view
     * that the user requested to sent the e-mail.
     *
     * @param callback The operation to perform when the e-mail is sent.
     */
    void sendRequested(Callback callback) {
        // Prepare the new e-mail bean by copying sandboxed values
        Email email = new Email();
        email.addAllRecipients(recipients.get());
        email.setSubject(subject.get());
        email.setBody(body.get());

        // Request the model to send the e-mail and to perform
        // callback action when send operation completes
        model.send(email, callback);
    }


    /**
     * Informs controllers in the same package about the status
     * of the e-mails the user is composing.
     *
     * @return The bindings that holds such status.
     */
    BooleanBinding isEmailWellFormed() {
        // The only check that's needed is that the e-mail has at least one recipient
        return recipients.sizeProperty().greaterThan(0);
    }


    /**
     * Computes the new subject.
     *
     * @param prefix The prefix that must be prepended to the previous subject.
     * @param email The e-mail from which to extract the previous subject from.
     * @return The computed new subject.
     */
    private String computeNewSubject(String prefix, Email email) {
        String newSubject = "";
        String prevSubject = email.getSubject();
        if (prevSubject != null && !prevSubject.isBlank()) {
            newSubject = prefix + ": " + prevSubject;
        }
        return newSubject;
    }

}