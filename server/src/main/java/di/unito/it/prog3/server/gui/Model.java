package di.unito.it.prog3.server.gui;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;

public class Model {

    private final ReadOnlyListWrapper<Log> logs;

    public Model() {
        logs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
    }

    public ReadOnlyListProperty<Log> logsProperty() {
        return logs.getReadOnlyProperty();
    }

}
