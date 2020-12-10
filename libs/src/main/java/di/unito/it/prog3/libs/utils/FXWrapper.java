package di.unito.it.prog3.libs.utils;

import javafx.scene.Parent;

import java.util.List;

public final class FXWrapper<Controller extends ControllerBase<?>> {

    private final Controller controller;
    private final Parent content;

    FXWrapper(Parent content, Controller controller) {
        this.controller = controller;
        this.content = content;
    }

    public Parent getContent() {
        return content;
    }

    public Controller getController() {
        return controller;
    }

}