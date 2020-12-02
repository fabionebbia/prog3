package di.unito.it.prog3.client.engine;

import di.unito.it.prog3.client.model.Model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {

    private final Map<Screen, Scene> screens;
    private final Model model;
    private final Stage stage;

    public ScreenManager(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;

        screens = new HashMap<>();
    }

    public void setScreen(Screen screen) throws IOException {
        screens.putIfAbsent(screen, screen.buildScene(this));
        stage.setScene(screens.get(screen));
        stage.setTitle(screen.getTitle());
    }

    public <T> T loadFXML(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        T content = loader.load();
        Controller controller = loader.getController();
        controller.init(this, model);
        return content;
    }

    public void show() {
        stage.show();
    }

}