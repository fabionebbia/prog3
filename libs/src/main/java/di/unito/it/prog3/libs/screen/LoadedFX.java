package di.unito.it.prog3.libs.screen;

import javafx.scene.Node;

class LoadedFX<M, T extends Node, C extends Controller<M>> {
    private final C controller;
    private final T content;

    LoadedFX(T content, C controller) {
        this.controller = controller;
        this.content = content;
    }

    T getContent() {
        return content;
    }

     C getController() {
        return controller;
    }
}