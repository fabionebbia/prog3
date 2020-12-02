package di.unito.it.prog3.server;

public class Controller {

    private Model model;

    protected void init(Model model) {
        if (this.model == null) {
            this.model = model;
        } else throw new IllegalStateException("Controller already initialized");


    }

}
