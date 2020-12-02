package di.unito.it.prog3.client.wipscreen.screentest;

import di.unito.it.prog3.client.wipscreen.WIPScreen;
import di.unito.it.prog3.client.wipscreen.WIPScreenManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class WIPMainScreen extends WIPScreen  {

    @Override
    protected String getTitle() {
        return "E-mails";
    }

    @Override
    protected void setupControl() {

    }

    @Override
    protected Scene loadScene(WIPScreenManager screenManager) throws IOException {
        BorderPane borderPane = screenManager.loadFXML("/main-screen-wip-test.fxml", this);
        Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getBounds();
        return new Scene(borderPane, screenBounds.getWidth() / 3, screenBounds.getHeight());
    }
}
