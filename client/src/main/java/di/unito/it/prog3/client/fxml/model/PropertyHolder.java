package di.unito.it.prog3.client.fxml.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface PropertyHolder<T> {

    T get(Property<T> property);

    void set(Property<T> property, T value);

    ReadOnlyStringProperty statusMessageProperty();

    ReadOnlyObjectProperty<T> statusProperty();

    interface Property<T> {

    }

}
