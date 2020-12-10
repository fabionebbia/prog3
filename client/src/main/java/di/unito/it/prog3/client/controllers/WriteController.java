package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.Recipient;
import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.libs.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class WriteController extends Controller implements ListChangeListener<String> {

    @FXML private GridPane gridPane;
    @FXML private TextField subjectField;
    @FXML private GridPane recipientsFlowPaneContainer;
    @FXML private FlowPane flowPane;
    @FXML private Button addButton;
    @FXML private TextField recipientField;
    @FXML private TextArea bodyTextArea;

    private ListProperty<String> recipientsProperty;


    @Override
    protected void setupControl() {
        recipientsProperty = model.currentEmailProperty().recipientsProperty();
        recipientsProperty.addListener(this);

        subjectField.textProperty().bindBidirectional(model.currentEmailProperty().subjectProperty());
        bodyTextArea.textProperty().bindBidirectional(model.currentEmailProperty().bodyProperty());

        Utils.bindVisibility(recipientsProperty.sizeProperty().greaterThan(0), flowPane);

        // TODO remove (use css instead: some padding)
        recipientsFlowPaneContainer.vgapProperty().bind(Bindings.createDoubleBinding(
                () -> recipientsProperty.size() > 0 ? 10d : 0d, recipientsProperty
        ));
    }

    @FXML
    private void addRecipient() {
        String newRecipient = recipientField.getText();
        recipientsProperty.add(newRecipient);
        recipientField.setText("");
    }

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
                        recipient.emailProperty().bind(Bindings.stringValueAt(recipientsProperty, offset));
                        recipient.onRemove(e -> recipientsProperty.remove(offset));
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
    }
}
