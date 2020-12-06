package di.unito.it.prog3.client.fxml.model;

import javafx.beans.property.*;

public interface StatusHolder {

    BaseStatus getStatus();

    void setStatus(BaseStatus newStatus);

    ReadOnlyStringProperty statusMessageProperty();

    ReadOnlyObjectProperty<BaseStatus> statusProperty();

    void onStatus(BaseStatus status, OnStatusListener listener);

    interface OnStatusListener {
        void perform();
    }
}
