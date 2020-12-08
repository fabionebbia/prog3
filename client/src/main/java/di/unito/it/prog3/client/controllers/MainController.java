package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.model.ClientStatus;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.screen.SuperController;
import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;

public class MainController extends SuperController<Model> {

    private static final String BASE_CLASS = "status-circle";
    private static final String CONNECTED_MODIFIER = "connected";

    private final Timeline timeline;

    @FXML private BorderPane container;
    @FXML private Circle statusCircle;
    @FXML private Label statusLabel;
    @FXML private Label emailLabel;
    @FXML private Label serverLabel;

    public MainController() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e ->
                    CssUtils.toggleModifier(statusCircle, BASE_CLASS, CONNECTED_MODIFIER)
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    @Override
    protected void setupControl() {
        // Shows client status message in the status bar
        statusLabel.textProperty().bind(model.userProperty());

        // Makes status circle react accordingly to client status changes
        model.clientStatusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (newStatus == CONNECTED) {
                timeline.playFromStart();
            } else if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.stop();
            }
        });

        // Shows client status message in the status bar
        statusLabel.textProperty().bind(
                Bindings.createStringBinding(this::mapStatusToMessage, model.clientStatusProperty())
        );

        // Shows logged user email address in the status bar
        emailLabel.textProperty().bind(model.userProperty());

        // Shows server address in the status bar
        serverLabel.textProperty().bind(model.serverURLProperty());
    }

    @Override
    protected void onFirstSet() {
        loadView("list-view");
    }

    @Override
    protected void setView(Node view) {
        container.setCenter(view);
    }

    private String mapStatusToMessage() {
        ClientStatus newStatus = model.clientStatusProperty().get();
        switch (newStatus) {
            case CONNECTED:
                return "Connected";
            case UNREACHABLE_SERVER:
                return "Cannot reach server";
            default:
                return "";
        }
    }

}
