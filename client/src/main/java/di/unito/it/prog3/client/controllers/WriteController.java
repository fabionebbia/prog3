package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.Recipient;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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

    private ListProperty<String> recipientsProperty;
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
/*
    @Override
    public void onChanged(Change<? extends String> c) {
        ObservableList<Node> children = flowPane.getChildren();

        while (c.next()) {
            if (c.wasAdded()) {
                List<? extends String> addedSubList = c.getAddedSubList();
                for (int i = 0; i < addedSubList.size(); i++) {
                    int offset = c.getFrom() + i;
                    try {
                        Recipient recipient = new Recipient();
                        recipient.emailProperty().bind(Bindings.stringValueAt(recipients, offset));
                        recipient.onRemove(e -> recipients.removeIf());
                        recipient.onClick(e -> recipientField.textProperty().bind(recipient.emailProperty()));
                        children.add(recipient);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (c.wasRemoved()) {
                children.remove(children.size() - 1);
            }
        }
    }*/

    @Override
    void onDisplayed() {
        gridPane.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && canAddRecipient()) {
                addRecipient();
            }
        });
        recipients.clear();
        subject.set("");
        body.set("");
    }

    @Override
    void onHidden() {
        gridPane.getScene().setOnKeyPressed(null);
    }

    void sendRequested() {
        Email email = new Email();
        System.out.println(recipients.get());
        email.addAllRecipients(recipients.get());
        email.setSubject(subject.get());
        email.setBody(body.get());
        model.send(email);
    }

}
