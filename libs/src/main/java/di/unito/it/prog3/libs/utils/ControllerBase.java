package di.unito.it.prog3.libs.utils;

public abstract class ControllerBase<M> {

    protected M model;

    public void init(M model) {
        if (this.model != null) {
            throw new IllegalStateException("Controller already initialized");
        }

        if (model == null) {
            throw new IllegalArgumentException("Null model");
        }

        this.model = model;

        setupControl();
    }

    protected abstract void setupControl();

}
