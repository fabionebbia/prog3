package di.unito.it.prog3.client;

import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.client.screen.Screen;
import di.unito.it.prog3.client.screen.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

import static di.unito.it.prog3.libs.utils.Debug.DEBUG;

public class MailClientApp extends Application {

    private Model model;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (DEBUG) {
            // TODO usarla. perchè l'ho messa actually?
        }

        /*Map<String, String> parameters = getParameters().getNamed();
        String username = parameters.get("user");
        String server = parameters.get("server");
        String port = parameters.get("port");*/

        model = new Model();

        ScreenManager screenManager = new ScreenManager(primaryStage, model);

        /*String automaticLogin = parameters.getOrDefault("automatic-login", "false");
        if (Boolean.parseBoolean(automaticLogin)) {
            model.login();
        }*/

        if (Boolean.parseBoolean(System.getProperty("automatic-login"))) {
            model.getClient().login(
                    System.getProperty("server"),
                    Integer.parseInt(System.getProperty("port")),
                    System.getProperty("email")
            );
            screenManager.setScreen(Screen.MAIN);
        } else {
            screenManager.setScreen(Screen.LOGIN);
        }

        screenManager.show();
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
