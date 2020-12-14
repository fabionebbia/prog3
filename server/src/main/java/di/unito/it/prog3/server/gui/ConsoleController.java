package di.unito.it.prog3.server.gui;


import di.unito.it.prog3.libs.utils.ControllerBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ConsoleController extends ControllerBase<Model> {

    private ReadOnlyBooleanProperty isWindowFocused;

    @FXML private ListView<Log> console;


    @Override
    protected void setupControl() {
        // Setup console list view cell factory to display logs
        console.setCellFactory(logs -> new ListCell<>() {
            @Override
            protected void updateItem(Log log, boolean empty) {
                super.updateItem(log, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                } else {
                    setWrapText(false);
                    setText(log.getMessage());
                    if (log.isError()) {
                        setStyle("-fx-text-fill: #db392c");
                    }
                }
            }
        });

        // Bind list view items to the logs property
        console.itemsProperty().bind(model.logsProperty());

        // Scroll to latest log if the server window is not selected
        model.logsProperty().addListener((ListChangeListener<Log>) c -> {
            if (isWindowFocused != null && !isWindowFocused.get()) {
                int nLogs = model.logsProperty().size();
                if (nLogs > 0) {
                    console.scrollTo(nLogs - 1);
                }
            }
        });
    }


    // Received the reference to the stage focused property
    public void bindFocus(ReadOnlyBooleanProperty isWindowFocused) {
        this.isWindowFocused = isWindowFocused;
    }

}
