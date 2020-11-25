package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.fxml.BaseController;
import di.unito.it.prog3.client.fxml.ScreenManager;
import di.unito.it.prog3.client.fxml.css.CssUtils;
import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.client.views.GuiClass;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class StatusBarController extends BaseController<Model> {

    @FXML
    private Circle clientStatusCircle;

    @FXML
    private Label clientStatusLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label serverLabel;

    @Override
    public void init(ScreenManager screenManager, Model model) {
        super.init(screenManager, model);

        // Shows client status message in the status bar
        clientStatusLabel.textProperty().bind(model.clientStatusMessageProperty());

        // Makes status circle react accordingly to client status changes
        model.clientStatusProperty().addListener(new CircleStatusListener());

        // Shows logged user email address in the status bar
        emailLabel.textProperty().bind(model.emailAddressProperty());

        // Shows server address in the status bar
        serverLabel.textProperty().bind(model.serverAddressProperty());

        /* TODO remove: used for testing
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> model.setStatus(MALFORMED_EMAIL_ADDRESS));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); */
    }

    private class CircleStatusListener implements ChangeListener<ClientStatus> {

        private final Timeline timeline;

        private CircleStatusListener() {
            timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e ->
                    CssUtils.toggle(clientStatusCircle, GuiClass.STATUS_CIRCLE__CONNECTED))
            );
            timeline.setCycleCount(Animation.INDEFINITE);
        }

        @Override
        public void changed(ObservableValue<? extends ClientStatus> observableValue,
                            ClientStatus oldStatus,
                            ClientStatus newStatus) {
            // Stops the blinking animation if it was running, ie: if oldStatus == CONNECTED
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
            }

            switch (newStatus) {
                case CONNECTED ->
                        timeline.playFromStart();
                case IDLE ->
                        CssUtils.ensureOnlyOfGroup(clientStatusCircle, GuiClass.STATUS_CIRCLE__IDLE);
                case UNREACHABLE_SERVER ->
                        CssUtils.ensureOnlyOfGroup(clientStatusCircle, GuiClass.STATUS_CIRCLE__UNREACHABLE_SERVER);
            }
        }
    }
}
