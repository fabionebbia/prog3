package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.EmailPreview;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.utils.Callback;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.concurrent.Callable;

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
            }

            tabPane.getTabs().get(oldValue.intValue()).setContent(null);
            tabPane.getTabs().get(newValue.intValue()).setContent(listView);
        });

        listView.setCellFactory(emailListView -> {
            EmailPreview preview = new EmailPreview();
            preview.setOnMouseClicked(this);
            return preview;
        });

        listView.setItems(model.receivedQueue().getValue());
        tabPane.getTabs().get(0).setContent(listView);
        tabPane.getSelectionModel().selectFirst();

        /*tabPane.getSelectionModel().selectedItemProperty().addListener((selectedObservable, oldSelected, newSelected) -> {
            selectedObservable.getValue().setContent(null);
            newSelected.setContent(listView);
        });*/

        /*DoubleBinding tabWidthBinding = Bindings.createDoubleBinding(
                () -> tabPane.widthProperty().get() / Queue.values().length, tabPane.widthProperty()
        );

        tabPane.tabMaxWidthProperty().bind(tabWidthBinding);
        tabPane.tabMinWidthProperty().bind(tabWidthBinding);*/

        /*Platform.runLater(() -> {
            Scene scene = tabPane.getScene();
            double width = scene.getWidth() / Queue.values().length;
            tabPane.setTabMinWidth(width);
            tabPane.setTabMaxWidth(width);
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

    void onEmailDoubleClick(Callback doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }

    void selectTab(int index) {
        if (index < 0 || index > tabPane.getTabs().size()) {
            throw new IllegalArgumentException("Tab index out of bounds");
        }
        tabPane.getSelectionModel().select(index);
    }

    static int RECEIVED_TAB = 0;
    static int SENT_TAB = 1;

}
