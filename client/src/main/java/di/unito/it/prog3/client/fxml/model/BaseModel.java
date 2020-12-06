package di.unito.it.prog3.client.fxml.model;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseModel implements StatusHolder {

    private final ReadOnlyObjectWrapper<BaseStatus> status;
    private final ReadOnlyStringWrapper statusMessage;
    private final Map<BaseStatus, List<OnStatusListener>> onStatusListeners;

    protected BaseModel(BaseStatus initialStatus) {
        status = new ReadOnlyObjectWrapper<>(initialStatus);
        statusMessage = new ReadOnlyStringWrapper(initialStatus.getMessage());
        onStatusListeners = new HashMap<>();

        status.addListener((statusObservable, oldStatus, newStatus) -> {
            statusMessage.set(newStatus.getMessage());
            System.out.println("aehwoeigjo");

            List<OnStatusListener> toBeCalled = onStatusListeners.get(newStatus);
            if (toBeCalled != null) toBeCalled.forEach(OnStatusListener::perform);
        });
    }

    @Override
    public BaseStatus getStatus() {
        return status.get();
    }

    @Override
    public void setStatus(BaseStatus newStatus) {
        Platform.runLater(() -> status.set(newStatus));
    }

    @Override
    public void onStatus(BaseStatus status, OnStatusListener listener) {
        onStatusListeners.putIfAbsent(status, new ArrayList<>());
        onStatusListeners.get(status).add(listener);
    }

    @Override
    public ReadOnlyStringProperty statusMessageProperty() {
        return statusMessage.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<BaseStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

}
