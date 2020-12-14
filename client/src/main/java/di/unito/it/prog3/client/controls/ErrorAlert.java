package di.unito.it.prog3.client.controls;

import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ErrorAlert extends Alert {

    private static final ButtonType cancelButton = new ButtonType("Close client", ButtonBar.ButtonData.CANCEL_CLOSE);
    private String dots;

    public ErrorAlert() {
        super(AlertType.ERROR, null, cancelButton);

        setTitle("Server connection error");
        setHeaderText("Cannot reach server");
        setContentText("Retrying...");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #db392c");
        getDialogPane().setStyle("-fx-font-size: 15px");
        setGraphic(progressIndicator);

        initStyle(StageStyle.UTILITY);
    }

}
