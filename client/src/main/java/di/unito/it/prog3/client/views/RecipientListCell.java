package di.unito.it.prog3.client.views;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class RecipientListCell extends ListCell<String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setText(item);

        TextField emailField = new TextField(item);
        Button removeButton = new Button("Remove");
        setGraphic(new HBox(emailField, removeButton));
    }
}
