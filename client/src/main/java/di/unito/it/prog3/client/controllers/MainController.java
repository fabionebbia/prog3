package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.fxml.BaseController;
import di.unito.it.prog3.client.fxml.ScreenManager;
import di.unito.it.prog3.client.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController extends BaseController<Model> {

    @FXML public MenuBar menuBar;
    @FXML public HBox toolbar;
    @FXML public VBox emailViewer;
    @FXML private HBox statusBar;

    @FXML private MenuBarController menuBarController;
    @FXML private ToolbarController toolbarController;
    @FXML private EmailViewerController emailViewerController;
    @FXML private StatusBarController statusBarController;

    public MainController(Model model) {
        init(null, model);
    }

    @Override
    public void init(ScreenManager screenManager, Model model) {
        super.init(screenManager, model);
    }
}
