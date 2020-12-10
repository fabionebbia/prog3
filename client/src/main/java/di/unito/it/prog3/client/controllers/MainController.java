package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.libs.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static di.unito.it.prog3.client.controllers.MainController.View.QUEUES;
import static di.unito.it.prog3.client.controllers.MainController.View.WRITE;
import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;

public class MainController extends Controller {

    // Root node
    @FXML private BorderPane borderPane;

    // Toolbar buttons
    @FXML private Button backButton;
    @FXML private Button writeButton;
    @FXML private Button forwardButton;
    @FXML private Button replyButton;
    @FXML private Button replyAllButton;
    @FXML private Button sendButton;
    @FXML private Button deleteButton;

    // Status bar controls
    @FXML private Circle statusCircle;
    @FXML private Label serverLabel;
    @FXML private Label userLabel;

    // Status bar circle blinker
    private final Timeline statusCircleBlinker;


    // Cached subviews
    private FXWrapper<QueueViewController> queueView;
    private FXWrapper<WriteController> writeView;
    private FXWrapper<ReadController> readView;

    // Active subview
    private final ObjectProperty<View> currentView;


    public MainController() {
        currentView = new SimpleObjectProperty<>();

        statusCircleBlinker = new Timeline(
                new KeyFrame(Duration.seconds(0.1),
                        e -> CssUtils.toggleModifier(statusCircle, "status-circle", "connected")
                )
        );
        statusCircleBlinker.setCycleCount(8);
        statusCircleBlinker.setOnFinished(
                event -> CssUtils.ensureClassSetGroupExclusive(statusCircle, "status-circle--idle")
        );
    }

    @Override
    protected void setupControl() {
        loadSubviews();

        setupToolbar();

        setupStatusBar();

        // Setup view change listener to manipulate the GUI
        currentView.addListener((observable, oldView, newView) -> {
            FXWrapper<?> next = switch (newView) {
                case QUEUES -> queueView;
                case READ -> readView;
                case WRITE -> writeView;
            };
            Parent nextContent = next.getContent();
            borderPane.setCenter(nextContent);
        });

        // Start on the
        currentView.set(QUEUES);
    }

    private void loadSubviews() {
        WrappedFXMLLoader loader = new WrappedFXMLLoader();

        queueView = loader.load("/screens/main/queues.fxml");
        queueView.getController().init(model);
        // Change to read view when the user double-clicks an e-mail preview
        queueView.getController().onEmailDoubleClick(() -> currentView.set(View.READ));

        writeView = loader.load("/screens/main/write.fxml");
        writeView.getController().init(model);

        readView = loader.load("/screens/main/read.fxml");
        readView.getController().init(model);
    }

    private void setupToolbar() {
        // Hide Back button on root view
        Utils.bindVisibility(currentView.isNotEqualTo(QUEUES), backButton);

        // Show Write button on root view only
        Utils.bindVisibility(currentView.isEqualTo(QUEUES), writeButton);

        // Hide Forward/Reply/ReplyAll buttons when writing a new e-mail
        // Otherwise, disable them if no e-mail is selected
        Utils.bindVisibility(
                currentView.isNotEqualTo(WRITE),
                model.isCurrentEmailSet(),
                forwardButton, replyButton, replyAllButton
        );

        // Show Send button only when writing a new e-mail
        Utils.bindVisibility(currentView.isEqualTo(WRITE), sendButton);

        // Disable Delete/Discard button on root view if no e-mail is selected
        // Change button text based on the current view
        deleteButton.disableProperty().bind(currentView.isEqualTo(QUEUES).and(model.isCurrentEmailSet().not()));
        deleteButton.textProperty().bind(Bindings.createStringBinding(
                () -> currentView.isEqualTo(WRITE).get() ? "Discard" : "Delete", currentView
        ));
    }

    private void setupStatusBar() {
        serverLabel.textProperty().bind(model.serverProperty());
        userLabel.textProperty().bind(model.userProperty());

        // Restarts blinking animation on client connected
        model.clientStatusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (newStatus == CONNECTED) {
                statusCircleBlinker.playFromStart();
            }
        });
    }

    @FXML
    private void back() {
        currentView.set(QUEUES);
    }

    @FXML
    private void write() {
        currentView.set(WRITE);
    }

    @FXML
    private void forward() {
    }

    @FXML
    private void reply() {
    }

    @FXML
    private void replyAll() {
    }

    @FXML
    private void send() {

    }

    @FXML
    private void delete() {

    }

    // Subviews
    enum View {
        QUEUES, READ, WRITE
    }

}