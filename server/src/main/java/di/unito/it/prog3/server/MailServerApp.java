package di.unito.it.prog3.server;


import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
import di.unito.it.prog3.server.gui.ConsoleController;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Set;

import static di.unito.it.prog3.libs.utils.Utils.DEBUG;

public class MailServerApp extends Application {

    private final Server server = new Server();
    private final Thread serverThread = new Thread(server, "Server Thread");

    @Override
    public void start(Stage stage) {
        Model model = new Model();

        server.init(model, getParameters());
        serverThread.start();

        FXWrapper<ConsoleController> gui = new WrappedFXMLLoader("/screens/main/index.fxml").load();
        gui.getController().init(model);
        gui.getController().bindFocus(stage.focusedProperty());

        /*stage.setOnCloseRequest(e -> {
            Platform.exit();
        });*/

        stage.setOnHidden(e -> {
            Platform.exit();
        });

        stage.setScene(new Scene(gui.getContent()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        synchronized (server) {
            DEBUG("Server shutdown requested");
            server.shutdown();
            DEBUG("Server shutdown");
            DEBUG("Server await completed");
        }

        DEBUG("Server shutdown");
        serverThread.interrupt();
        serverThread.join();
        DEBUG("FX stopped");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
