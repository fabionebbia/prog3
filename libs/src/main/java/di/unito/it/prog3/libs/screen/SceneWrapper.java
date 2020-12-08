package di.unito.it.prog3.libs.screen;

import javafx.scene.Scene;


class SceneWrapper<M> {

    private final SuperController<M> controller;
    private final Scene scene;

    SceneWrapper(Scene scene, SuperController<M> controller) {
        this.scene = scene;
        this.controller = controller;
    }

    SuperController<M> getController() {
        return controller;
    }

    Scene getScene() {
        return scene;
    }
}
