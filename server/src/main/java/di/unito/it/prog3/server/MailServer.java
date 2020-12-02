package di.unito.it.prog3.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MailServer extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();
        /*ScreenManager screenManager = new ScreenManager(primaryStage);
        screenManager.loadAndSetScreen(new ServerScreen(), model);
        screenManager.show();*/

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server-screen.fxml"));
        Parent content = loader.load();

        Controller controller = loader.getController();
        controller.init(model);

        primaryStage.setScene(new Scene(content));
        primaryStage.setTitle("Server");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
