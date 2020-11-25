package di.unito.it.prog3.client.fxml.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseModel<T extends Enum<T> & BaseStatus> implements StatusHolder<T> {

    private final ReadOnlyObjectWrapper<T> status;
    private final ReadOnlyStringWrapper statusMessage;
    private final Map<T, List<OnStatusListener>> onStatusListeners;

    protected BaseModel(T initialStatus) {
        status = new ReadOnlyObjectWrapper<>(initialStatus);
        statusMessage = new ReadOnlyStringWrapper(initialStatus.getMessage());
        onStatusListeners = new HashMap<>();

        status.addListener((statusObservable, oldStatus, newStatus) -> {
            statusMessage.set(newStatus.getMessage());

            List<OnStatusListener> toBeCalled = onStatusListeners.get(newStatus);
            if (toBeCalled != null) toBeCalled.forEach(OnStatusListener::perform);
        });
    }

    @Override
    public T getStatus() {
        return status.get();
    }

    @Override
    public void setStatus(T newStatus) {
        status.set(newStatus);
    }

    @Override
    public void onStatus(T status, OnStatusListener listener) {
        onStatusListeners.putIfAbsent(status, new ArrayList<>());
        onStatusListeners.get(status).add(listener);
    }

    @Override
    public ReadOnlyStringProperty statusMessageProperty() {
        return statusMessage.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyObjectProperty<T> statusProperty() {
        return status.getReadOnlyProperty();
    }
}
