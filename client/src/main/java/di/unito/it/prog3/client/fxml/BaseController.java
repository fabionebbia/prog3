package di.unito.it.prog3.client.fxml;

public abstract class BaseController<T> implements Controller<T> {

    protected ScreenManager screenManager;
    protected T model;

    @Override
    public void init(ScreenManager screenManager, T model) {
        if (this.screenManager == null && this.model == null) {
            this.screenManager = screenManager;
            this.model = model;
        } else throw new IllegalStateException("Controller already initialized");
    }
}
