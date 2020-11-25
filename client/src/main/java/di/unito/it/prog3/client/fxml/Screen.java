package di.unito.it.prog3.client.fxml;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;

public class Screen {

    private final String title;
    private final Scene scene;

    public Screen(String title, Parent sceneContent, double width, double height) {
        scene = new Scene(sceneContent, width, height);
        this.title = title;
    }

    protected Scene getScene() {
        return scene;
    }

    protected String getTitle() {
        return title;
    }

    public static class ComputedSizeScreen extends Screen {
        public ComputedSizeScreen(String title, Parent sceneContent) {
            super(title, sceneContent, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        }
    }
}
