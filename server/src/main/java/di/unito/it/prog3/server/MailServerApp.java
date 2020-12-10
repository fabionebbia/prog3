package di.unito.it.prog3.server;


import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
import di.unito.it.prog3.server.gui.Controller;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.server.Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MailServerApp extends Application {

    private Server server;

    @Override
    public void start(Stage stage) throws Exception {
        Model model = new Model();
        server = new Server(model, 2525);

        FXWrapper<Controller> gui = new WrappedFXMLLoader().load("/screens/main/index.fxml");
        gui.getController().init(model);

        Scene scene = new Scene(gui.getContent());
        stage.setScene(scene);
        stage.show();

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
