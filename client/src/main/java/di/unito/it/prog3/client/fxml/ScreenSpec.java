package di.unito.it.prog3.client.fxml;

import java.io.IOException;

public class ScreenSpec<M> {

    private final ScreenLoader<M> screenLoader;

    public ScreenSpec(ScreenLoader<M> screenLoader) {
        this.screenLoader = screenLoader;
    }

    protected ScreenLoader<M> getLoader() {
        return screenLoader;
    }

    public interface ScreenLoader<M> {
        Screen load(ScreenManager screenManager, M model) throws IOException;
    }
}
