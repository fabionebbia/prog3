package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.fxml.BaseController;
import di.unito.it.prog3.client.fxml.ScreenManager;
import di.unito.it.prog3.client.model.Model;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.concurrent.atomic.AtomicReference;

public class BrowserController extends BaseController<Model> {

    @FXML
    private TabPane tabPane;

    private Tab currentTab;

    @Override
    public void init(ScreenManager screenManager, Model model) {
        super.init(screenManager, model);

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
