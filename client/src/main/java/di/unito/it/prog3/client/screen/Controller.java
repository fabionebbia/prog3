package di.unito.it.prog3.client.screen;

import di.unito.it.prog3.client.model.Model;
import javafx.scene.Node;

public abstract class Controller {

    protected ScreenManager screenManager;
    protected Model model;

    void init(ScreenManager screenManager, Model model) {
        if (initialized()) {
            throw new IllegalStateException("Controller already initialized");
        }

        if (screenManager == null) {
            throw new IllegalArgumentException("Null screen manager");
        }
        if (model == null) {
            throw new IllegalArgumentException("Null model");
        }

        this.screenManager = screenManager;
        this.model = model;

        setupControl();
    }

    private boolean initialized() {
        return screenManager != null || model != null;
    }

    protected void setView(Node view) {
        throw new UnsupportedOperationException("This screen does not support subviews");
    }

    protected abstract void setupControl();

}
