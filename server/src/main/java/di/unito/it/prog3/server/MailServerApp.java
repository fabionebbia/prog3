package di.unito.it.prog3.server;


import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
import di.unito.it.prog3.server.gui.ConsoleController;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MailServerApp extends Application {

    private final Server server = new Server();

    @Override
    public void start(Stage stage) {
        Model model = new Model();

        FXWrapper<ConsoleController> gui = new WrappedFXMLLoader("/screens/main/index.fxml").load();
        gui.getController().init(model);

        stage.setOnCloseRequest(e -> Platform.exit());
        stage.setScene(new Scene(gui.getContent()));
        stage.show();

        server.start(model, getParameters());
    }

    @Override
    public void stop() {
        synchronized (server) {
            try {
                server.shutdown();
                server.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
