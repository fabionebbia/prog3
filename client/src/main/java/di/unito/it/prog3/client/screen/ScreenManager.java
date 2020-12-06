package di.unito.it.prog3.client.screen;

import di.unito.it.prog3.client.model.Model;
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

public class ScreenManager {

    private final Map<String, SceneWrapper> scenes;
    private final Stage stage;
    private final Model model;
    private String activeScene;

    public ScreenManager(Stage stage, Model model) {
        this.stage = stage;
        this.model = model;

        scenes = new HashMap<>();
    }

    public void setScene(String sceneKey) {
        SceneWrapper wrapper = scenes.get(sceneKey);
        Scene scene;

        if (wrapper == null) {
            String resource = "/screens/" + sceneKey + "/index.fxml";
            try {
                LoadedFX<Parent> fx = loadFXML(resource);

                Controller controller = fx.getController();
                Parent content = fx.getContent();

                scene = new Scene(content);

                wrapper = new SceneWrapper(scene, controller);
                scenes.put(sceneKey, wrapper);
            } catch (IOException e) {
                throw new RuntimeException("Could not set scene " + sceneKey, e);
            }
        } else {
            scene = wrapper.getCachedScene();
        }

        activeScene = sceneKey;
        stage.setScene(scene);
    }

    public void setView(String viewKey) {
        String[] keyParts = viewKey.split("/");

        if (keyParts.length != 2) {
            throw new RuntimeException("Malformed view identifier " + viewKey);
        }

        SceneWrapper wrapper = scenes.get(keyParts[0]);

        if (wrapper == null || !keyParts[0].equals(activeScene)) {
            throw new RuntimeException("Need to set parent screen `" + keyParts[0] + "` before " + viewKey);
        }

        ViewWrapper view;
        Node node;
        Controller controller;
        if (!wrapper.hasViewCached(keyParts[1])) {
            String resource = "/screens/" + keyParts[1] + ".fxml";
            try {
                LoadedFX<Node> fx = loadFXML(resource);

                controller = fx.getController();
                node = fx.getContent();

                view = new ViewWrapper(node, controller);
                wrapper.setCachedView(viewKey, view);
            } catch (IOException e) {
                throw new RuntimeException("Could not set view " + viewKey, e);
            }
        } else {
            view = wrapper.getCachedView(viewKey);
            controller = view.getController();
            node = view.getNode();
        }

        controller.setView(node);
    }

    public <T extends Node> LoadedFX<T> loadFXML(String resource) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        T content = loader.load();

        Controller controller = loader.getController();
        controller.init(this, model);

        return new LoadedFX<>(content, controller);
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

    public void stop() {
        model.stop();
    }


    private static class LoadedFX<T extends Node> {
        private final Controller controller;
        private final T content;

        private LoadedFX(T content, Controller controller) {
            this.controller = controller;
            this.content = content;
        }

        private T getContent() {
            return content;
        }

        private Controller getController() {
            return controller;
        }
    }

}
