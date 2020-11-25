package di.unito.it.prog3.client.fxml.css;

import di.unito.it.prog3.client.fxml.model.BaseStatus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.Styleable;

public abstract class ClassBindListener<T extends BaseStatus> implements ChangeListener<T> {

    protected final Styleable styleable;

    public ClassBindListener(Styleable styleable) {
        this.styleable = styleable;
    }

    @Override
    public void changed(ObservableValue<? extends T> observableStatus, T oldStatus, T newStatus) {
        String newClass = getNewClass(newStatus);
        styleable.getStyleClass().add(newClass);
    }

    public abstract String getNewClass(T newStatus);

    public static abstract class UniqueClassBindListener<T extends BaseStatus> extends ClassBindListener<T> {

        public UniqueClassBindListener(Styleable styleable) {
            super(styleable);
        }

        @Override
        public void changed(ObservableValue<? extends T> observableStatus, T oldStatus, T newStatus) {
            String newClass = getNewClass(newStatus);
            CssUtils.ensureOnlyOfGroup(styleable, newClass);
        }
    }
}
