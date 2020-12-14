package di.unito.it.prog3.client.controls;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.StageStyle;

public class UnreachableServerAlert extends Alert {

    private static final ButtonType cancelButton = new ButtonType("Close client", ButtonBar.ButtonData.CANCEL_CLOSE);


    public UnreachableServerAlert() {
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
