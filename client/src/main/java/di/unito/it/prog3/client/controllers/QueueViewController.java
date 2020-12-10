package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.EmailPreview;
import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Callback;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class QueueViewController extends Controller implements EventHandler<MouseEvent> {

    private static final int NONE = -1;
    private int selectedIndex = NONE;

    @FXML private TabPane tabPane;
    @FXML private ListView<Email> listView;

    private SelectionModel<Email> selectionModel;

    private Callback doubleClickCallback;
    
    @Override
    protected void setupControl() {
        selectionModel = listView.getSelectionModel();

        selectionModel.selectedItemProperty().addListener(
                (observable, oldEmail, newEmail) -> model.setCurrentEmail(newEmail)
        );

        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0 -> listView.setItems(model.receivedQueue().getValue());
                case 1 -> listView.setItems(model.sentQueue().getValue());
                default -> throw new IllegalStateException("Unexpected value: " + newValue.intValue());
            };
            //listView.setItems(currentQueue.getValue());
            System.out.println(oldValue + " -> " + newValue);
            tabPane.getTabs().get(oldValue.intValue()).setContent(null);
            tabPane.getTabs().get(newValue.intValue()).setContent(listView);
        });

        listView.setCellFactory(emailListView -> {
            EmailPreview preview = new EmailPreview();
            preview.setOnMouseClicked(this);
            return preview;
        });

        System.out.println(model.receivedQueue().getValue());
        listView.setItems(model.receivedQueue().getValue());
        tabPane.getTabs().get(0).setContent(listView);
        tabPane.getSelectionModel().selectFirst();

        /*tabPane.getSelectionModel().selectedItemProperty().addListener((selectedObservable, oldSelected, newSelected) -> {
            selectedObservable.getValue().setContent(null);
            newSelected.setContent(listView);
        });*/
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            int newSelectedIndex = selectionModel.getSelectedIndex();
            //electedIndex = (selectedIndex == newSelectedIndex) ? NONE : newSelectedIndex;

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
                selectionModel.clearSelection();
            }

            /*if (clickCount == 1) {
                int newSelectedIndex = selectionModel.getSelectedIndex();

                selectedIndex = (selectedIndex == newSelectedIndex) ? NONE : newSelectedIndex;
            } else if (clickCount == 2) {
                if (doubleClickCallback != null) {
                    doubleClickCallback.call();
                }
                selectedIndex =
            } else return;

            if (selectedIndex == NONE) {
                selectionModel.clearSelection();
            }*/
        }
    }

    protected void onEmailDoubleClick(Callback doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }

}
