package di.unito.it.prog3.client.views;

import di.unito.it.prog3.client.fxml.Screen.TopLevelScreen;
import di.unito.it.prog3.client.fxml.ScreenManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class BrowseScreen extends TopLevelScreen {

    private static final String TITLE = "E-mail browser";

    private BorderPane borderPane;

    protected void setContent(Parent content) {
        borderPane.setCenter(content);
    }

    @Override
    protected String getTitle() {
        return TITLE;
    }

    @Override
    protected Scene buildScene(ScreenManager screenManager) throws IOException {
        borderPane = (BorderPane) screenManager.loadFXML("/test.fxml");
        Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getBounds();
        return new Scene(borderPane, screenBounds.getWidth() / 3, screenBounds.getHeight());
    }
}
