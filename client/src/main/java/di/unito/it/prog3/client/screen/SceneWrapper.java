package di.unito.it.prog3.client.screen;

import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;


class SceneWrapper {

    private final Map<String, ViewWrapper> viewWrappers;
    private final Controller controller;
    private final Scene scene;

    SceneWrapper(Scene scene, Controller controller) {
        this.scene = scene;
        this.controller = controller;
        viewWrappers = new HashMap<>();
    }

    Scene getCachedScene() {
        return scene;
    }

    boolean hasViewCached(String viewKey) {
        return viewWrappers.containsKey(viewKey);
    }

    ViewWrapper getCachedView(String viewKey) {
        if (!hasViewCached(viewKey)) {
            // TODO check before hasViewCached
        }
        return viewWrappers.get(viewKey);
    }

    void setCachedView(String viewKey, ViewWrapper viewWrapper) {
        viewWrappers.put(viewKey, viewWrapper);
    }
}
