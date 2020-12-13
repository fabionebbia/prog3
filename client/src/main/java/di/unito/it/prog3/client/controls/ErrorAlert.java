package di.unito.it.prog3.client.controls;

import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.libs.model.Error;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;

import java.net.ConnectException;

import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

public class ErrorAlert extends Alert {

    private static final ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    private ProgressIndicator progressIndicator;

    public ErrorAlert() {
        super(AlertType.ERROR, null, cancelButton);
        initModality(Modality.APPLICATION_MODAL);
        progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: #db392c");

        getDialogPane().setStyle("-fx-font-size: 15px");
    }

    public void bindClientStatus(ObservableValue<ClientStatus> clientStatus) {
        clientStatus.addListener((observable, oldStatus, newStatus) -> {
            if (newStatus == UNREACHABLE_SERVER) {
                setTitle("Server connection error");
                setHeaderText(null);
                setContentText("Cannot reach server, retrying..");
                setGraphic(progressIndicator);

                setOnCloseRequest(event -> Platform.exit());

                if (!isShowing()) {
                    showAndWait();
                }
            } else {
                setOnCloseRequest(null);
                hide();
            }
        });
    }

    public void showError(Error error) {
        setTitle(error.getTitle());
        setHeaderText(error.getHeader());
        setContentText(error.getContent());
        setOnCloseRequest(null);
        showAndWait();
    }

    public void showError(Throwable t) {
        if (!(t instanceof ConnectException)) {
            showError(new Error("Error", null, t.getMessage()));
        }
    }

}
