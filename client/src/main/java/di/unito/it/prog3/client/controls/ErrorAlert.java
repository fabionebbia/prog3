package di.unito.it.prog3.client.controls;

import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.libs.model.Error;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.ConnectException;

import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

public class ErrorAlert extends Alert {

    private static final ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private final ObservableValue<ClientStatus> clientStatus;
    private final ProgressIndicator progressIndicator;
    private final Stage stage;

    public ErrorAlert(Stage stage, ObservableValue<ClientStatus> clientStatus) {
        super(AlertType.ERROR, null, cancelButton);
        this.stage = stage;
        this.clientStatus = clientStatus;

        setTitle("Server connection error");
        setContentText("Cannot reach server\nRetrying..");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #db392c");

        getDialogPane().setStyle("-fx-font-size: 15px");
        setGraphic(progressIndicator);

        setOnHidden(event -> {
            if (clientStatus.getValue() == UNREACHABLE_SERVER) {
                Platform.exit();
            }
        });
    }

}
