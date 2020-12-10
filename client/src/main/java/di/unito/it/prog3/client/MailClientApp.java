package di.unito.it.prog3.client;

import di.unito.it.prog3.client.trashed.LoginScreenController;
import di.unito.it.prog3.client.controllers.MainController;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.Perform;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
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
    public void start(Stage stage) throws Exception {
        Perform.init(executor = Executors.newSingleThreadExecutor());
        Model model = new Model();

        WrappedFXMLLoader loader = new WrappedFXMLLoader();

        FXWrapper<MainController> main = loader.load("/screens/main/index.fxml");
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        Scene mainScene = new Scene(main.getContent(), screenWidth / 3, screenHeight);

        FXWrapper<LoginScreenController> login = loader.load("/screens/login/index.fxml");
        Scene loginScene = new Scene(login.getContent());

        login.getController().init(model);
        login.getController().onSuccessfulLogin(() -> stage.setScene(mainScene));

        main.getController().init(model);

        stage.setScene(loginScene);
        stage.show();
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
