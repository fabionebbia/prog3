package di.unito.it.prog3.client.screen;

import di.unito.it.prog3.client.model.Model;

public abstract class Controller {

    protected ScreenManager screenManager;
    protected Model model;

    protected void init(ScreenManager screenManager, Model model) {
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

    protected abstract void setupControl();

}
