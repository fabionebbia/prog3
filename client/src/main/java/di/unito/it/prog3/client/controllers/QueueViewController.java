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

    // Used to represent no selection
    private static final int NONE = -1;

    // Currently selected list view index
    private int selectedIndex;

    // EmailPreview double click callback
    private Callback doubleClickCallback;

    // JavaFX controls
    @FXML private ListView<Email> listView;
    @FXML private TabPane tabPane;


    /**
     * Called on queue view first load.
     * Sets up all queue view controls and sets some listeners.
     */
    @Override
    protected void setupControl() {
        // Sets the list view cell factory to produce EmailPreview list cells
        // and sets this controller as their mouse clicked event handler
        listView.setCellFactory(emailListView -> {
            EmailPreview preview = new EmailPreview();
            preview.setOnMouseClicked(this);
            return preview;
        });

        // At startup, show Received queue e-mails in list view
        listView.setItems(model.receivedQueue());

        // When the user changes selected e-mail in the list view,
        // update the model to keep track of the currently selected e-mail
        listView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldEmail, newEmail) -> model.setCurrentEmail(newEmail)
        );

        // At startup, show list view in the first tab (Received tab) and select it
        tabPane.getTabs().get(0).setContent(listView);
        tabPane.getSelectionModel().selectFirst();

        // Sets list view content on tab pane index selection change
        //      Received queue in the first tab
        //      Sent queue in the second tab
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            switch (newIndex.intValue()) {
                case 0 -> listView.setItems(model.receivedQueue());
                case 1 -> listView.setItems(model.sentQueue());
                default -> throw new IllegalStateException("Unexpected tab selected:" + newIndex.intValue());
            }

            // Moves the list view from previous tab to new tab
            tabPane.getTabs().get(oldIndex.intValue()).setContent(null);
            tabPane.getTabs().get(newIndex.intValue()).setContent(listView);
        });

        // Startup with no selection
        selectedIndex = NONE;
    }


    /**
     * Called when the user clicks (or double-clicks) on EmailPreview.
     *
     * @param event JavaFX generated click event.
     */
    @Override
    public void handle(MouseEvent event) {
        // If it was left click
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            // Get the new selected index
            int newSelectedIndex = listView.getSelectionModel().getSelectedIndex();

            if (selectedIndex != newSelectedIndex) {
                // If selected index changed,
                // deselect previous e-mail and select the new one
                selectedIndex = newSelectedIndex;
            } else {
                // If selected index did not change,
                // deselect previous e-mail
                selectedIndex = NONE;
            }

            if (event.getClickCount() == 2) {
                // If it was double click and double click callback is set, call it
                if (doubleClickCallback != null) {
                    doubleClickCallback.call();
                }
            } else if (selectedIndex == NONE) {
                // If it wasn't double click and selected index
                // was previously set to none, clear list view selection
                listView.getSelectionModel().clearSelection();
            }
        }
    }


    /**
     * Called by controllers in the same package to set
     * EmailPreviews double click callback.
     *
     * @param doubleClickCallback The double click callback.
     */
    void onEmailDoubleClick(Callback doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }

}
