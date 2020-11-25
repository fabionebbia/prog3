package di.unito.it.prog3.client.views;

import di.unito.it.prog3.client.controllers.MainController;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.client.fxml.Screen;
import di.unito.it.prog3.client.fxml.Screen.ComputedSizeScreen;
import di.unito.it.prog3.client.fxml.ScreenSpec;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public interface ClientScreen {

    ScreenSpec<Model> LOGIN = new ScreenSpec<>((screenManager, model) -> {
        Parent screenContent = screenManager.loadFxml("/login-form.fxml", model);
        return new ComputedSizeScreen("Login", screenContent);
    });

    ScreenSpec<Model> BROWSE = new ScreenSpec<>((screenManager, model) -> {
        BorderPane borderPane = new BorderPane();

        Parent menuBar = screenManager.loadFxml("/menu-bar.fxml", model);
        Parent toolbar = screenManager.loadFxml("/toolbar.fxml", model);
        VBox topVBox = new VBox(menuBar, toolbar);
        borderPane.setTop(topVBox);

        Parent browser = screenManager.loadFxml("/browser.fxml", model);
        borderPane.setCenter(browser);

        Parent emailViewer = screenManager.loadFxml("/email-viewer-test.fxml", model);
        borderPane.setCenter(emailViewer);

        Parent statusBar = screenManager.loadFxml("/status-bar.fxml", model);
        borderPane.setBottom(statusBar);

        String style = screenManager.loadStylesheet("/ui.css");
        borderPane.getStylesheets().add(style);

        Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getBounds();
        double width = screenBounds.getWidth() / 3;
        double height = screenBounds.getHeight();
        return new Screen("E-mail browser", borderPane, width, height);
    });


    ScreenSpec<Model> TEST = new ScreenSpec<>(((screenManager, model) -> {
        /*FXMLLoader loader = new FXMLLoader(ClientScreen.class.getResource("/test.fxml"));
        Parent borderPane = loader.load();

        MainController controller = loader.getController();
        controller.init(screenManager, model);*/

        Parent borderPane = screenManager.test("/test.fxml", Model.class, model);

        Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getBounds();
        double width = screenBounds.getWidth() / 3;
        double height = screenBounds.getHeight();
        return new Screen("test", borderPane, width, height);
    }));

}
