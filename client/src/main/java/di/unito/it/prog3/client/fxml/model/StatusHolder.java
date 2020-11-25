package di.unito.it.prog3.client.fxml.model;

import javafx.beans.property.*;

public interface StatusHolder<T extends Enum<T> & BaseStatus> {

    T getStatus();

    void setStatus(T newStatus);

    ReadOnlyStringProperty statusMessageProperty();

    ReadOnlyObjectProperty<T> statusProperty();

    void onStatus(T status, OnStatusListener listener);

    interface OnStatusListener {
        void perform();
    }
}
