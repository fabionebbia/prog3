package di.unito.it.prog3.client.controls;

import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.libs.model.Error;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.stage.*;

import java.net.ConnectException;

import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

public class ErrorAlert extends Alert {

    private static final ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

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
