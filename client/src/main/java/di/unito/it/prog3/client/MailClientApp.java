package di.unito.it.prog3.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import di.unito.it.prog3.client.controllers.MainController;
import di.unito.it.prog3.client.controls.ErrorAlert;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net2.*;
import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
import javafx.application.Application;
import javafx.application.Platform;
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
        main.getController().init(model, stage);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        Scene mainScene = new Scene(main.getContent(), screenWidth / 3, screenHeight);

        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(mainScene);
        stage.show();

        Request request = model.getClient().newRequest(RequestType.SEND)
                .addRecipient("ciao@come.va")
                .setSubject("Subject")
                .setBody("Body")
                .build();

        JsonMapper json = new JsonMapper();
        json.registerSubtypes(
                LoginRequest.class,
                ReadRequest.class,
                SendRequest.class,
                OpenRequest.class,
                DeletionRequest.class
        );
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
