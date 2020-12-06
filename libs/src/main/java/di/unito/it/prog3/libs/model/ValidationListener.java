package di.unito.it.prog3.libs.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public interface ValidationListener extends ChangeListener<Boolean> {

    void changed(boolean isValid);

    @Override
    default void changed(ObservableValue<? extends Boolean> observable, Boolean wasValid, Boolean isValid) {
        changed(isValid);
    }
}
