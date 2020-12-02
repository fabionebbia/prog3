package di.unito.it.prog3.client.controllers.screens;

import di.unito.it.prog3.client.screen.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainScreenController extends Controller {

    @FXML
    private BorderPane screen;

    private Map<View, Parent> views;

    public MainScreenController() {
        views = new HashMap<>();
    }

    @Override
    protected void setupControl() {
        setView(View.EMAIL_LIST);
    }

    private void setView(View view) {
        if (!views.containsKey(view)) {
            try {
                Parent parent = screenManager.loadFXML(view.fxmlFile);
                screen.setCenter(parent);
                views.put(view, parent);
            } catch (IOException e) {
                throw new RuntimeException("Could not set view " + view.name());
            }
        }
    }

    private enum View {
        EMAIL_LIST("/email-list-view.fxml");

        private final String fxmlFile;

        View(String fxmlFile) {
            this.fxmlFile = fxmlFile;
        }
    }

}
