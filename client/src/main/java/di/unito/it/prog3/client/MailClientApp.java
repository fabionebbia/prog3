package di.unito.it.prog3.client;

import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.client.screen.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;


public class MailClientApp extends Application {

    private ScreenManager screenManager;

    @Override
    public void start(Stage stage) {
        Model model = new Model();
        screenManager = new ScreenManager(stage, model);
        screenManager.setScene("login");
        screenManager.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        screenManager.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
