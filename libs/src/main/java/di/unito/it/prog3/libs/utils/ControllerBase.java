package di.unito.it.prog3.libs.utils;

public abstract class ControllerBase<Model> {

    protected Model model;

    public void init(Model model) {
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
