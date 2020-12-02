package di.unito.it.prog3.libs.forms;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Control;

import static di.unito.it.prog3.libs.forms.FormUtils.Action.HIDE;
import static di.unito.it.prog3.libs.forms.FormUtils.Action.SHOW;

public final class FormUtils {

    private FormUtils() {}

    public static BooleanBinder show(Control control) {
        return makeBinder(SHOW, control);
    }

    public static BooleanBinder hide(Control control) {
        return makeBinder(HIDE, control);
    }

    private static BooleanBinder makeBinder(Action action, Control control) {
        return new BooleanBinder(action, control);
    }

    public static class BooleanBinder {
        private final Control control;
        private Action action;

        private BooleanBinder(Action action, Control control) {
            this.control = control;
            this.action = action;
        }

        public void when(BooleanBinding binding) {
            switch (action) {
                case SHOW:
                case HIDE:
                    control.visibleProperty().bind(binding);
                    control.managedProperty().bind(binding);
                    break;
            }
        }

        public void onlyWhen(BooleanBinding binding) {
            switch (action) {
                case SHOW:
                    action = HIDE;
                    break;
                case HIDE:
                    action = SHOW;
                    break;
            }
            when(binding.not());
        }
    }

    enum Action {
        SHOW, HIDE
    }

}
