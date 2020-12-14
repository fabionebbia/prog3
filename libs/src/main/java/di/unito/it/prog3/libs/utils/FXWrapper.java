package di.unito.it.prog3.libs.utils;

import javafx.scene.Parent;

public final class FXWrapper<C extends ControllerBase<?>> {

    private final C controller;
    private final Parent content;

    FXWrapper(Parent content, C controller) {
        this.controller = controller;
        this.content = content;
    }

    public Parent getContent() {
        return content;
    }

    public C getController() {
        return controller;
    }

}