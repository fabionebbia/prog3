package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.EmailPreview;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.screen.Controller;
import di.unito.it.prog3.libs.email.Email;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.MouseEvent;

public class EmailListViewController extends Controller<Model> implements EventHandler<MouseEvent> {

    @FXML
    private ListView<Email> listView;

    private static final int NONE = -1;
    private int selectedIndex;

    public EmailListViewController() {
        selectedIndex = NONE;
    }

    @Override
    protected void setupControl() {
        listView.itemsProperty().bind(model.emailsProperty());
        listView.setCellFactory(emailListView -> new EmailPreview(this::handleDoubleClick));
        listView.setOnMouseClicked(this);
    }

    private void handleDoubleClick(int itemIndex) {
        System.out.println("Clicked " + itemIndex);
    }

    @Override
    public void handle(MouseEvent e) {
        SelectionModel<Email> selectionModel = listView.getSelectionModel();
        int newSelectedIndex = selectionModel.getSelectedIndex();

        selectedIndex = (selectedIndex == newSelectedIndex) ? NONE : newSelectedIndex;

        if (selectedIndex == NONE) {
            selectionModel.clearSelection();
        }
    }

}
