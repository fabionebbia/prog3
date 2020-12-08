package di.unito.it.prog3.server;

import di.unito.it.prog3.libs.screen.ScreenManager;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

public class MailServerApp extends Application {

    private Server server;

    @Override
    public void start(Stage stage) {
        Model model = new Model();
        server = new Server(model, 2525);

        ScreenManager<Model> screenManager = new ScreenManager<>(stage, model);
        screenManager.loadScene("main");
        screenManager.show();

        new Thread(server, "server").start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        server.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
