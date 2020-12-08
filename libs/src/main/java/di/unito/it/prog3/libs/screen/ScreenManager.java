package di.unito.it.prog3.libs.screen;

import di.unito.it.prog3.libs.model.Error;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ScreenManager<M> {

    private final Map<String, SceneWrapper<M>> scenes;
    private final Stage stage;
    private final M model;

    private SceneWrapper<M> active;

    public ScreenManager(Stage stage, M model) {
        this.stage = stage;
        this.model = model;

        scenes = new HashMap<>();
    }

    public void loadScene(String root) {
        loadScene(root, Scene::new);
    }

    public void loadScene(String root, Function<Parent, Scene> sceneBuilder) {
        if (scenes.containsKey(root)) {
            throw new IllegalStateException(root + " scene  already loaded");
        }
        try {
            String resource = "/screens/" + root + "/index.fxml";
            LoadedFX<M, Parent, SuperController<M>> fx = loadFXML(resource);

            SuperController<M> controller = fx.getController();
            controller.setRoot(root);
            controller.init(this, model);

            Parent content = fx.getContent();
            Scene scene = sceneBuilder.apply(content);

            SceneWrapper<M> wrapper = new SceneWrapper<>(scene, controller);
            scenes.put(root, wrapper);
            setScene(wrapper, true);
        } catch (IOException e) {
            throw new RuntimeException("Could not load scene " + root, e);
        }
    }

    public void setScene(String root) {
        if (!scenes.containsKey(root)) {
            throw new IllegalStateException(root + " scene not loaded yet");
        }
        setScene(scenes.get(root), false);
    }

    private void setScene(SceneWrapper<M> next, boolean firstTime) {
        SuperController<M> nextController = next.getController();
        Scene scene = next.getScene();
        SceneWrapper<M> past = active;

        if (firstTime) {
            nextController.onFirstSet();
        } else {
            nextController.onSet();
        }

        stage.setScene(scene);

        if (past != null) {
            past.getController().onUnset();
        }

        active = next;
    }

    public <T extends Node, C extends Controller<M>> LoadedFX<M, T, C> loadFXML(String resource) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        return new LoadedFX<>(loader.load(), loader.getController());
    }

    public void displayError(Error error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle(error.getTitle());
            alert.setHeaderText(error.getHeader());
            alert.setContentText(error.getContent());
            alert.showAndWait();
        });
    }

    public void show() {
        stage.show();
    }

}
