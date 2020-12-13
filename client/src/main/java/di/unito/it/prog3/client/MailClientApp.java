package di.unito.it.prog3.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import di.unito.it.prog3.client.controllers.MainController;
import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.model.Error;
import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class MailClientApp extends Application {

    private Model model;

    @Override
    public void start(Stage stage) {
        model = new Model(getParameters());

        WrappedFXMLLoader loader = new WrappedFXMLLoader();
        FXWrapper<MainController> main = loader.load("/screens/main/index.fxml");
        main.getController().init(model);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        Scene mainScene = new Scene(main.getContent(), screenWidth / 3, screenHeight);

        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(mainScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        model.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
