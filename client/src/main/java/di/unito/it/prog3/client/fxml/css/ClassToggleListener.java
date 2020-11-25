package di.unito.it.prog3.client.fxml.css;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.Styleable;

public class ClassToggleListener implements ChangeListener<Boolean> {

    private final Styleable styleable;
    private final String styleClass;

    public ClassToggleListener(Styleable styleable, String styleClass) {
        this.styleable = styleable;
        this.styleClass = styleClass;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean previous, Boolean current) {
        CssUtils.conditionallySetClass(styleable, styleClass, current);
    }
}