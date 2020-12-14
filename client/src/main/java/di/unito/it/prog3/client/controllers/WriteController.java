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

    @FXML private GridPane gridPane;
    @FXML private TextField subjectField;
    @FXML private GridPane recipientsFlowPaneContainer;
    @FXML private FlowPane flowPane;
    @FXML private Button addButton;
    @FXML private TextField recipientField;
    @FXML private TextArea bodyTextArea;

    private final SetProperty<String> recipients;
    private final StringProperty subject;
    private final StringProperty body;

    private BooleanBinding canAddRecipient;


    public WriteController() {
        recipients = new SimpleSetProperty<>(FXCollections.observableSet());
        subject = new SimpleStringProperty();
        body = new SimpleStringProperty();
    }


    @Override
    protected void setupControl() {
        subjectField.textProperty().bindBidirectional(subject);
        bodyTextArea.textProperty().bindBidirectional(body);

        recipients.addListener((SetChangeListener<String>) change -> {
            flowPane.getChildren().clear();
            for (String recipient : recipients) {
                try {
                    Recipient node = new Recipient();
                    node.setEmail(recipient);
                    node.onRemoveButtonPressed(e -> recipients.remove(recipient));
                    node.onClick(e -> {
                        recipients.remove(recipient);
                        recipientField.textProperty().set(recipient);
                    });
                    flowPane.getChildren().add(node);
                } catch (IOException e) {
                    throw new RuntimeException("Could not load graphic components", e);
                }
            }
        });

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
