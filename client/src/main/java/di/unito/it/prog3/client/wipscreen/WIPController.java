package di.unito.it.prog3.client.wipscreen;

import di.unito.it.prog3.client.model.Model;

public abstract class WIPController {

    protected WIPScreenManager screenManager;
    protected Model model;

    private boolean initialized;

    // TODO exception messages
    protected void initControl(WIPScreenManager screenManager, Model model, boolean alsoSetup) {
        if (initialized) throw new IllegalStateException();
        if (screenManager == null) throw new IllegalArgumentException();
        if (model == null) throw new IllegalArgumentException();

        this.screenManager = screenManager;
        this.model = model;
        initialized = true;

        if (alsoSetup) {
            setupControl();
        }
    }

    protected abstract void setupControl();

}
