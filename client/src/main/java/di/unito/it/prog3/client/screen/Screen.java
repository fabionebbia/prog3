package di.unito.it.prog3.client.screen;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;

import java.io.IOException;

public enum Screen {

    LOGIN("Login", screenManager -> {
        Parent content = screenManager.loadFXML("/login-screen.fxml");
        return new Scene(content, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
    }),

    MAIN("E-mails", screenManager -> {
        Parent content = screenManager.loadFXML("/main-screen.fxml");
        Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getBounds();
        return new Scene(content, screenBounds.getWidth() / 3, screenBounds.getHeight());
    });

    private final ScreenBuilder builder;
    private final String title;

    Screen(String title, ScreenBuilder builder) {
        this.title = title;
        this.builder = builder;
    }

    String getTitle() {
        return title;
    }

    Scene buildScene(ScreenManager screenManager) throws IOException {
        return builder.build(screenManager);
    }

    private interface ScreenBuilder {
        Scene build(ScreenManager screenManager) throws IOException;
    }
}
