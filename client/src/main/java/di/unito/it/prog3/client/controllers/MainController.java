package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.ErrorAlert;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.model.Error;
import di.unito.it.prog3.libs.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.ConnectException;

import static di.unito.it.prog3.client.controllers.MainController.View.QUEUES;
import static di.unito.it.prog3.client.controllers.MainController.View.WRITE;
import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;
import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

public class MainController extends Controller {

    private final ErrorAlert errorAlert;

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

        errorAlert = new ErrorAlert();
    }

    @Override
    protected void setupControl() {
        errorAlert.bindClientStatus(model.getClient().statusProperty());
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> handleException(exception));

        loadSubviews();

        setupToolbar();

        setupStatusBar();

        // Setup view change listener to manipulate the GUI
        currentView.addListener((observable, oldView, newView) -> {
            if (oldView != null) {
                FXWrapper<? extends Controller> previous = getWrapper(oldView);
                previous.getController().onHidden();
            }
            FXWrapper<? extends Controller> next = getWrapper(newView);
            borderPane.setCenter(next.getContent());
            next.getController().onDisplayed();
        });

        currentView.set(QUEUES);
    }

    private void loadSubviews() {
        WrappedFXMLLoader loader = new WrappedFXMLLoader();

        queueView = loader.load("/screens/main/queues.fxml");
        queueView.getController().init(model);
        // Change to read view when the user double-clicks an e-mail preview
        queueView.getController().onEmailDoubleClick(() -> {
            currentView.set(View.READ);
            model.openCurrentEmail();
        });

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
        Utils.bindVisibility(
                currentView.isEqualTo(WRITE),
                writeView.getController().isEmailWellFormed(),
                sendButton
        );

        // Disable Delete/Discard button on root view if no e-mail is selected
        // Change button text based on the current view
        deleteButton.disableProperty().bind(currentView.isEqualTo(QUEUES).and(model.isCurrentEmailSet().not()));
        deleteButton.textProperty().bind(Bindings.createStringBinding(
                () -> currentView.isEqualTo(WRITE).get() ? "Discard" : "Delete", currentView
        ));
    }

    private void setupStatusBar() {
        serverLabel.textProperty().bind(model.getClient().serverProperty());
        userLabel.textProperty().bind(model.getClient().userProperty());

        // TODO
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert unreachableAlert = new Alert(
                AlertType.ERROR,
                "Cannot reach server\nRetrying..",
                cancelButton
        );


        unreachableAlert.setTitle("Server connection error");
        unreachableAlert.setOnHidden(event -> {
            if (model.getClient().statusProperty().get().equals(UNREACHABLE_SERVER)) {
                Platform.exit();
            }
        });

        // Restarts blinking animation on client connected
        model.getClient().statusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (unreachableAlert.isShowing()) {
                unreachableAlert.hide();
            }

            CssUtils.ensureClassSetGroupExclusive(
                    statusCircle,
                    "status-circle--" + newStatus.name().toLowerCase().replace('_', '-')
            );

            switch (newStatus) {
                case CONNECTED -> statusCircleBlinker.playFromStart();
                //case UNREACHABLE_SERVER -> handleException(new Exception());//unreachableAlert.showAndWait();
            }
        });
    }

    @FXML
    private void back() {
        currentView.set(QUEUES);
    }

    @FXML
    private void write() {
        model.clearCurrentEmail();
        currentView.set(WRITE);
        writeView.getController().open(WriteMode.NEW);
    }

    @FXML
    private void forward() {
        currentView.set(WRITE);
        writeView.getController().open(WriteMode.FORWARD);
    }

    @FXML
    private void reply() {
        currentView.set(WRITE);
        writeView.getController().open(WriteMode.REPLY);
    }

    @FXML
    private void replyAll() {
        currentView.set(WRITE);
        writeView.getController().open(WriteMode.REPLY_ALL);
    }

    @FXML
    private void send() {
        writeView.getController().sendRequested(() -> currentView.set(QUEUES));
        queueView.getController().selectTab(QueueViewController.SENT_TAB);
    }

    @FXML
    private void delete() {
        if (currentView.get().equals(QUEUES)) {
            Email.ID id = model.getCurrentEmail().getId();
            model.delete(id);
        }
    }

    @Override
    void handleException(Throwable t) {
        try {
            if (t instanceof ConnectException) errorAlert.showError(t);
            else new Error("", "", t.getMessage()).display();
            //getWrapper(currentView.get()).getController().handleException(t);
        } catch (Exception e) {
            e.printStackTrace();
            //new Error("", "", e.getMessage()).display();
        }
    }

    private FXWrapper<? extends Controller> getWrapper(View view) {
        return switch (view) {
            case QUEUES -> queueView;
            case READ -> readView;
            case WRITE -> writeView;
        };
    }

    // Subviews
    enum View {
        QUEUES, READ, WRITE
    }

}