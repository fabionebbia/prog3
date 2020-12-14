package di.unito.it.prog3.server.gui;


import di.unito.it.prog3.libs.utils.ControllerBase;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ConsoleController extends ControllerBase<Model> {

    @FXML
    private ListView<Log> console;

    @Override
    protected void setupControl() {
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
        console.itemsProperty().bind(model.logsProperty());

        model.logsProperty().addListener((ListChangeListener<Log>) c -> {
            while (c.next()) ; // do nothing, just consume the changes
            int nLogs = model.logsProperty().size();
            if (nLogs > 0) {
                console.scrollTo(nLogs - 1);
            }
        });
    }

}
