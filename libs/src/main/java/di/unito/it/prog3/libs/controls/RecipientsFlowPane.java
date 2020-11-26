package di.unito.it.prog3.libs.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

// Oracle - Creating a Custom Control with FXML
// url: https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm
public class RecipientsFlowPane extends GridPane implements ListChangeListener<String> {

    @FXML
    private FlowPane flowPane;

    @FXML
    private TextField recipientField;

    @FXML
    private Button addButton;

    private final ListProperty<String> recipientsProperty;

    public RecipientsFlowPane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls/recipients-flow-pane.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.load();

        recipientsProperty = new SimpleListProperty<>();
        recipientsProperty.addListener(this);

        addButton.setOnAction(e -> recipientsProperty.add(recipientField.getText()));
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

        // TODO remove (use css instead: some padding)
        if (children.size() > 0) setVgap(10);
        else setVgap(0);
    }

    private void trimChildren(int n) {
        // TODO check n >= 0 && n < currentChildrenSize
        ObservableList<Node> children = flowPane.getChildren();
        if (n < children.size()) {
            children.remove(n, children.size());
        }
    }

    public ListProperty<String> recipientsProperty() {
        return recipientsProperty;
    }

}
