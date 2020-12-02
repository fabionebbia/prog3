package di.unito.it.prog3.client.wipscreen;

import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class WIPScreen extends WIPController implements Initializable {

    private Scene scene;

    protected Scene getScene() throws IOException {
        if (scene == null) {
            scene = loadScene(screenManager);
        }
        return scene;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupControl();
    }

    protected abstract String getTitle();

    protected abstract Scene loadScene(WIPScreenManager screenManager) throws IOException;

}
