package di.unito.it.prog3.client.controls;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.StageStyle;

public class IncomingMessagesAlert extends Alert {

    public IncomingMessagesAlert() {
        super(Alert.AlertType.INFORMATION);

        Image image = new Image(getClass().getResource("/893229-email/png/033-inbox.png").toExternalForm());
        ImageView icon = new ImageView(image);
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        setGraphic(icon);

        setTitle("Inbox update");
        setHeaderText("Inbox");
        setContentText("Incoming messages!");

        getDialogPane().setStyle("-fx-font-size: 15px");
        initStyle(StageStyle.UTILITY);
    }

}
