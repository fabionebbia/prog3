package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.libs.screen.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class BrowserController extends Controller {

    @FXML
    private TabPane tabPane;

    private Tab currentTab;

    @Override
    public void setupControl() {
        currentTab = tabPane.getTabs().get(0);
        Platform.runLater(() -> tabPane.requestFocus());

        Label label = new Label("CIAO");
        tabPane.getSelectionModel().selectedItemProperty().addListener((selectedObservable, oldSelected, newSelected) -> {
            currentTab.setContent(null);
            newSelected.setContent(label);
            currentTab = newSelected;
        });
    }
}
