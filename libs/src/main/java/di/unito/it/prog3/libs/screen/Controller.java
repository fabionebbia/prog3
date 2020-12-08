package di.unito.it.prog3.libs.screen;

public abstract class Controller<M> {

    protected ScreenManager<M> screenManager;
    protected M model;

    void init(ScreenManager<M> screenManager, M model) {
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

    protected void onFirstSet() {}

    protected void onSet() {}

    protected void onUnset() {}

}
