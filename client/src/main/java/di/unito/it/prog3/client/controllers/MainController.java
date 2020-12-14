package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.UnreachableServerAlert;
import di.unito.it.prog3.client.controls.IncomingMessagesAlert;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.CssUtils;
import di.unito.it.prog3.libs.utils.FXWrapper;
import di.unito.it.prog3.libs.utils.Utils;
import di.unito.it.prog3.libs.utils.WrappedFXMLLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static di.unito.it.prog3.client.controllers.MainController.View.QUEUES;
import static di.unito.it.prog3.client.controllers.MainController.View.WRITE;
import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

public class MainController extends Controller {

    // Root container node
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

    // Cached subviews
    private FXWrapper<QueueViewController> queueView;
    private FXWrapper<WriteController> writeView;
    private FXWrapper<ReadController> readView;

    // Active subview
    private final ObjectProperty<View> currentView;

    // Status bar circle blinker
    private final Timeline statusCircleBlinker;

    // Alerts
    private IncomingMessagesAlert incomingAlert;
    private UnreachableServerAlert unreachableAlert;

    // Stage to initialize alerts' owner
    private Stage stage;


    /**
     * Creates the circle status blinking timeline.
     */
    public MainController() {
        statusCircleBlinker = new Timeline(
                new KeyFrame(Duration.seconds(0.1),
                        e -> CssUtils.toggleModifier(statusCircle, "status-circle", "connected")
                )
        );
        statusCircleBlinker.setCycleCount(8);

        // Resets status circle to idle class after animation completes
        statusCircleBlinker.setOnFinished(
                event -> CssUtils.ensureClassSetGroupExclusive(statusCircle, "status-circle--idle")
        );

        currentView = new SimpleObjectProperty<>();
    }


    /**
     * Initializes the model and passes reference to the stage.
     *
     * @param model The model.
     * @param stage The stage.
     */
    public void init(Model model, Stage stage) {
        super.init(model);
        this.stage = stage;
    }


    /**
     * Called on main screen first load.
     * Starts all control setup procedures, sets up some listeners and sets root view.
     */
    @Override
    protected void setupControl() {
        // Passes runtime exception to handleException method
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> handleException(exception));

        loadSubviews();

        setupToolbar();

        setupStatusBar();

        // Listen for receivedQueue changes to show an alert to the user when new e-mails are received
        model.receivedQueue().addListener((ListChangeListener<Email>) change -> {
            // change.next()                              - Trigger change inspection, required by JavaFX
            // change.getAddedSize() > 0                  - If e-mails were added
            // model.getClient().firstRequestSent().get() - If contact with server was already established
            if (change.next() && change.getAddedSize() > 0 && model.getClient().firstRequestSent().get()) {

                // If incomingAlert was newer displayed or isn't already showing
                if (incomingAlert == null || !incomingAlert.isShowing()) {

                    // Create a new IncomingMessagesAlert, init its owner
                    // so that it stays centered in the stage and show it
                    incomingAlert = new IncomingMessagesAlert();
                    incomingAlert.initOwner(stage);
                    incomingAlert.show();
                }
            }
        });

        // Setup view change listener to manipulate the GUI
        currentView.addListener((observable, oldView, newView) -> {
            if (oldView != null) {
                getWrapper(oldView).getController().onHidden();
            }

            FXWrapper<? extends Controller> next = getWrapper(newView);
            borderPane.setCenter(next.getContent());
            next.getController().onDisplayed();
        });

        // Sets current view after view change listener was added
        // so that onDisplayed and other view change events
        // can be properly triggered/handled
        currentView.set(QUEUES);
    }


    /**
     * Loads and caches subviews.
     */
    private void loadSubviews() {
        WrappedFXMLLoader loader = new WrappedFXMLLoader();

        queueView = loader.load("/screens/main/queues.fxml");
        queueView.getController().init(model);

        writeView = loader.load("/screens/main/write.fxml");
        writeView.getController().init(model);

        readView = loader.load("/screens/main/read.fxml");
        readView.getController().init(model);

        // Change to read view when the user double-clicks an e-mail preview
        queueView.getController().onEmailDoubleClick(() -> {
            currentView.set(View.READ);
            Email email = model.openCurrentEmail();
            readView.getController().showEmail(email);
        });
    }


    /**
     * Sets up toolbar controls.
     */
    private void setupToolbar() {
        // Hide Back button on root view
        Utils.bindVisibility(currentView.isNotEqualTo(QUEUES), backButton);

        // Show Write button on root view only
        Utils.bindVisibility(currentView.isEqualTo(QUEUES), writeButton);

        // Hide Forward/Reply/ReplyAll buttons when writing a new e-mail
        // and disable them if no e-mail is selected
        Utils.bindVisibility(
                currentView.isNotEqualTo(WRITE),
                model.isCurrentEmailSet(),
                forwardButton, replyButton, replyAllButton
        );

        // Show Send button only when writing a new e-mail
        // and disable it if e-mail is not ready to be sent
        Utils.bindVisibility(
                currentView.isEqualTo(WRITE),
                writeView.getController().isEmailWellFormed(),
                sendButton
        );

        // Disable Delete/Discard button on root view if no e-mail is selected
        deleteButton.disableProperty().bind(
                currentView.isEqualTo(QUEUES).and(model.isCurrentEmailSet().not())
        );

        // Change button text based on the current view
        deleteButton.textProperty().bind(Bindings.createStringBinding(
                () -> currentView.isEqualTo(WRITE).get() ? "Discard" : "Delete", currentView
        ));
    }


    /**
     * Sets up status bar controls.
     */
    private void setupStatusBar() {
        serverLabel.textProperty().bind(model.getClient().serverProperty());
        userLabel.textProperty().bind(model.getClient().userProperty());

        // Restarts blinking animation on client connected
        model.getClient().statusProperty().addListener((observableStatus, oldStatus, newStatus) -> {

            // Ensure status circle assumes the right color
            CssUtils.ensureClassSetGroupExclusive(
                    statusCircle, "status-circle--" + newStatus.name().toLowerCase().replace('_', '-')
            );

            // If unreachable alert is showing (iff oldStatus == UNREACHABLE_SERVER), hide it
            if (unreachableAlert != null && unreachableAlert.isShowing()) {
                unreachableAlert.hide();
            }

            switch (newStatus) {
                case CONNECTED ->
                    // Restart status circle blinking animation
                    statusCircleBlinker.playFromStart();

                case UNREACHABLE_SERVER -> {
                    // Create a new UnreachableServerAlert
                    unreachableAlert = new UnreachableServerAlert();

                    // If the user chooses not to wait for reconnection
                    // start an orderly shutdown process of the whole application
                    unreachableAlert.setOnHidden(event -> {
                        if (observableStatus.getValue() == UNREACHABLE_SERVER) {
                            Platform.exit();
                        }
                    });

                    // Init alert owner so that it stays centered
                    // in the stage and show it and show it
                    unreachableAlert.initOwner(stage);
                    unreachableAlert.showAndWait();
                }
            }
        });
    }


    /**
     * Called on Back button press.
     */
    @FXML
    private void back() {
        // Go back to queue view
        currentView.set(QUEUES);
    }


    /**
     * Called on Write button press.
     */
    @FXML
    private void write() {
        // Unset current e-mail in the model
        // so that the user can start fresh
        model.clearCurrentEmail();

        // Open write view
        currentView.set(WRITE);

        // Request child write view controller to setup
        // itself for writing a new e-mail
        writeView.getController().open(WriteMode.NEW);
    }


    /**
     * Called on Forward button press.
     */
    @FXML
    private void forward() {
        // Open write view
        currentView.set(WRITE);

        // Request child write view controller to setup
        // itself for executing an e-mail forward
        writeView.getController().open(WriteMode.FORWARD);
    }


    /**
     * Called on Reply button press.
     */
    @FXML
    private void reply() {
        // Open write view
        currentView.set(WRITE);

        // Request child write view controller to setup
        // itself for executing an e-mail reply
        writeView.getController().open(WriteMode.REPLY);
    }


    /**
     * Called on Reply All Button press.
     */
    @FXML
    private void replyAll() {
        // Open write view
        currentView.set(WRITE);

        // Request child write view controller to setup
        // itself for executing an e-mail reply all
        writeView.getController().open(WriteMode.REPLY_ALL);
    }


    /**
     * Called on Send button press.
     */
    @FXML
    private void send() {
        // Notify write view controller that the user requested
        // to send the e-mail and to go back to queues view
        // when the request execution completes
        writeView.getController().sendRequested(() -> currentView.set(QUEUES));
    }


    /**
     * Called when Delete button is pressed.
     */
    @FXML
    private void delete() {
        // If current view is not write view, i.e. the user is either
        //      - reading an e-mail in the read view
        //      - or selecting an e-mail in the queues view
        // ask the model to delete that e-mail
        if (currentView.get() != WRITE) {
            Email.ID id = model.getCurrentEmail().getId();
            model.delete(id);
        }

        // If the current view is writing view,
        // simply discard any changes by resetting to queue view
        currentView.set(QUEUES);
    }


    /**
     * Called then an exception occurs.
     * Displays its message in an error alert.
     *
     * @param t The exception.
     */
    void handleException(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.initOwner(stage);
        alert.setTitle(null);
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();

        if (Utils.debugEnabled) {
            t.printStackTrace();
        }
    }


    /**
     * Utility used for easier retrieval of a cached view wrapper.
     *
     * @param view The view whose wrapper must be retrieved.
     * @return The view wrapper;
     */
    private FXWrapper<? extends Controller> getWrapper(View view) {
        return switch (view) {
            case QUEUES -> queueView;
            case READ -> readView;
            case WRITE -> writeView;
        };
    }


    // The subviews
    enum View {
        QUEUES, READ, WRITE
    }

}