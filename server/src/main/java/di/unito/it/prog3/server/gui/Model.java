package di.unito.it.prog3.server.gui;

import di.unito.it.prog3.libs.store.EmailStore;
import di.unito.it.prog3.libs.store.LocalJsonEmailStore;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;

public class Model {

    private final ReadOnlyListWrapper<String> logs;

    private final EmailStore store;

    public Model() {
        logs = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

        store = new LocalJsonEmailStore("_store");
    }

    public ReadOnlyListProperty<String> logsProperty() {
        return logs.getReadOnlyProperty();
    }

}
