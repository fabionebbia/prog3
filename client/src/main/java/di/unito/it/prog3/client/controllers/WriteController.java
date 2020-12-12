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

    private SetProperty<String> recipients;
    private StringProperty subject;
    private StringProperty body;


    @Override
    protected void setupControl() {
        recipients = new SimpleSetProperty<>(FXCollections.observableSet());
        subject = new SimpleStringProperty();
        body = new SimpleStringProperty();

        // recipientsProperty = model.currentEmailProperty().recipientsProperty();
        // recipientsProperty.addListener(this);

        // subjectField.textProperty().bindBidirectional(model.currentEmailProperty().subjectProperty());
        // bodyTextArea.textProperty().bindBidirectional(model.currentEmailProperty().bodyProperty());
        subjectField.textProperty().bindBidirectional(subject);
        bodyTextArea.textProperty().bindBidirectional(body);

        recipients.addListener((SetChangeListener<String>) change -> {
            flowPane.getChildren().clear();
            for (String recipient : recipients) {
                try {
                    Recipient node = new Recipient();
                    node.setEmail(recipient);
                    node.onRemoveButtonPressed(e -> recipients.remove(recipient));
                    node.onClick(e -> recipientField.textProperty().set(recipient));
                    flowPane.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        addButton.disableProperty().bind(
                Bindings.createBooleanBinding(this::cannotAddRecipient, recipientField.textProperty())
        );

        // Utils.bindVisibility(recipientsProperty.sizeProperty().greaterThan(0), flowPane);
        Utils.bindVisibility(recipients.sizeProperty().greaterThan(0), flowPane);

        /* TODO remove (use css instead: some padding)
        recipientsFlowPaneContainer.vgapProperty().bind(Bindings.createDoubleBinding(
                () -> recipientsProperty.size() > 0 ? 10d : 0d, recipientsProperty
        )); */

        recipientsFlowPaneContainer.vgapProperty().bind(Bindings.createDoubleBinding(
                () -> recipients.size() > 0 ? 10d : 0d, recipients
        ));

        //bodyTextArea.lookup(".scroll-bar:vertical").setDisable(true);

    }

    private boolean canAddRecipient() {
        String newRecipient = recipientField.textProperty().get();
        return Emails.isWellFormed(newRecipient) && !recipients.contains(newRecipient);
    }

    private boolean cannotAddRecipient() {
        return !canAddRecipient();
    }

    @FXML
    private void addRecipient() {
        String newRecipient = recipientField.getText();
        recipients.add(newRecipient);
        recipientField.setText("");
    }

    @Override
    void onDisplayed() {
        gridPane.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && canAddRecipient()) {
                addRecipient();
            }
        });

        String newSubject = "";
        String newBody = "";
        if (model.isCurrentEmailSet().get()) { // forwarding
            Email email = model.getCurrentEmail();
            StringBuilder sb = new StringBuilder();
            sb.append("----------- Forwarded e-mail -----------")
                    .append("\nSubject: ").append(email.getSubject())
                    .append("\nFrom:    ").append(email.getSender())
                    .append("\nTo:      ").append(Utils.join(email.getRecipients()))
                    .append("\nDate:    ").append(Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(email.getTimestamp()))
                    .append("\n\n--------------- Message ----------------")
                    .append("\n").append(email.getBody());
            newBody = sb.toString();

            String prevSubject = email.getSubject();
            if (prevSubject != null && !prevSubject.isBlank()) {
                newSubject = "Fwd: " + prevSubject;
            }
        }

        recipientField.requestFocus();
        recipients.clear();
        subject.set(newSubject);
        body.set(newBody);
    }

    @Override
    void onHidden() {
        gridPane.getScene().setOnKeyPressed(null);
    }

    void open(WriteMode mode) {
        gridPane.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && canAddRecipient()) {
                addRecipient();
            }
        });

        switch (mode) {
            case NEW:
                recipientField.requestFocus();
                recipients.clear();
                subject.set("");
                body.set("");
                break;
            case FORWARD:
                openForward();
                break;
            case REPLY:
                openReply(false);
                break;
            case REPLY_ALL:
                openReply(true);
                break;
        }
    }

    private void openForward() {
        recipients.clear();

        Email email = model.getCurrentEmail();
        StringBuilder sb = new StringBuilder();
        sb.append("----------- Forwarded e-mail -----------")
                .append("\nSubject: ").append(email.getSubject())
                .append("\nFrom:    ").append(email.getSender())
                .append("\nTo:      ").append(Utils.join(email.getRecipients()))
                .append("\nDate:    ").append(Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(email.getTimestamp()))
                .append("\n\n--------------- Message ----------------")
                .append("\n").append(email.getBody());
        String newBody = sb.toString();

        String newSubject = "";
        String prevSubject = email.getSubject();
        if (prevSubject != null && !prevSubject.isBlank()) {
            newSubject = "Fwd: " + prevSubject;
        }

        subject.set(newSubject);
        body.set(newBody);
    }

    private void openReply(boolean all) {
        Email currentEmail = model.getCurrentEmail();

        recipients.clear();
        recipients.add(currentEmail.getSender());
        if (all) {
            recipients.addAll(currentEmail.getRecipients());
            if (recipients.size() > 1) {
                recipients.remove(model.getClient().userProperty().get());
            }
        }

        Email email = model.getCurrentEmail();
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n------------ Previous e-mail -----------")
                .append("\n").append(email.getSender())
                .append(" wrote \"").append(email.getSubject()).append("\"\non ")
                .append(Emails.VISUAL_TIMESTAMP_DATE_FORMAT.format(email.getTimestamp()))
                .append("\n\n\n").append(email.getBody());
        String newBody = sb.toString();

        String newSubject = "";
        String prevSubject = email.getSubject();
        if (prevSubject != null && !prevSubject.isBlank()) {
            newSubject = "Re: " + prevSubject;
        }

        subject.set(newSubject);
        body.set(newBody);

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

}
