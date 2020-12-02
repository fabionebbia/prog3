package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.libs.utils.CssUtils;
import di.unito.it.prog3.libs.utils.CssUtils.Styler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;

public class StatusBarController extends Controller implements ChangeListener<ClientStatus> {

    private static final String BASE_CLASS = "status-circle";
    private static final String CONNECTED_MODIFIER = "connected";

    @FXML
    private Circle statusCircle;

    @FXML
    private Label statusLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label serverLabel;

    private final Timeline timeline;

    public StatusBarController() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    CssUtils.toggleModifier(statusCircle, BASE_CLASS, CONNECTED_MODIFIER);
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    @Override
    public void setupControl() {
        // Shows client status message in the status bar
        statusLabel.textProperty().bind(model.clientStatusMessageProperty());

        // Makes status circle react accordingly to client status changes
        model.clientStatusProperty().addListener(this);

        // Shows logged user email address in the status bar
        // emailLabel.textProperty().bind(model.emailAddressProperty());
        emailLabel.textProperty().bind(model.getClient().userEmailProperty());

        // Shows server address in the status bar
        // serverLabel.textProperty().bind(model.serverAddressProperty());
        serverLabel.textProperty().bind(model.getClient().serverAddressProperty());


        Styler.style(statusCircle).bindWithModifier(BASE_CLASS, model.clientStatusProperty());
    }

    @Override
    public void changed(ObservableValue<? extends ClientStatus> observable,
                        ClientStatus oldStatus,
                        ClientStatus newStatus) {
        if (newStatus == CONNECTED) {
            timeline.playFromStart();
        } else if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }
    }

}
