package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.EmailPreview;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Callback;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class QueueViewController extends Controller implements EventHandler<MouseEvent> {

    private static final int NONE = -1;

    private Callback doubleClickCallback;
    private int selectedIndex = NONE;

    @FXML private ListView<Email> listView;
    @FXML private TabPane tabPane;


    @Override
    protected void setupControl() {
        listView.setCellFactory(emailListView -> {
            EmailPreview preview = new EmailPreview();
            preview.setOnMouseClicked(this);
            return preview;
        });

        listView.setItems(model.receivedQueue().getValue());
        tabPane.getTabs().get(0).setContent(listView);
        tabPane.getSelectionModel().selectFirst();

        listView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldEmail, newEmail) -> model.setCurrentEmail(newEmail)
        );

        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            switch (newIndex.intValue()) {
                case 0 -> listView.setItems(model.receivedQueue().getValue());
                case 1 -> listView.setItems(model.sentQueue().getValue());
                default -> throw new IllegalStateException("Unexpected tab selected:" + newIndex.intValue());
            }

            tabPane.getTabs().get(oldIndex.intValue()).setContent(null);
            tabPane.getTabs().get(newIndex.intValue()).setContent(listView);
        });
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            int newSelectedIndex = listView.getSelectionModel().getSelectedIndex();

            if (selectedIndex != newSelectedIndex) {
                selectedIndex = newSelectedIndex;
            } else {
                selectedIndex = NONE;
            }

            if (event.getClickCount() == 2) {
                if (doubleClickCallback != null) {
                    doubleClickCallback.call();
                }
            } else if (selectedIndex == NONE) {
                listView.getSelectionModel().clearSelection();
            }
        }
    }

    void onEmailDoubleClick(Callback doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }

}
