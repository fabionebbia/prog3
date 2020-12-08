package di.unito.it.prog3.client;

import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.screen.ScreenManager;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MailClientApp extends Application {

    private ExecutorService executor;

    @Override
    public void start(Stage stage) {
        Perform.init(executor = Executors.newSingleThreadExecutor());
        ScreenManager<Model> screenManager = new ScreenManager<>(stage, new Model(executor));

        if (System.getProperty("auto-login") != null) {
            screenManager.loadScene("main", parent -> {
                Rectangle2D computerScreen = Screen.getPrimary().getBounds();
                double width = computerScreen.getWidth() / 3;
                double height = computerScreen.getHeight();
                return new Scene(parent, width, height);
            });
        } else {
            screenManager.loadScene("login");
        }

        screenManager.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        executor.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
