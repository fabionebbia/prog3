package di.unito.it.prog3.libs.utils;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class WrappedFXMLLoader {

    private FXMLLoader loader;

    public WrappedFXMLLoader() { }

    public WrappedFXMLLoader(String location) {
        loader = new FXMLLoader(getClass().getResource(location));
    }

    public <C extends ControllerBase<?>> FXWrapper<C> load() {
        if (loader == null || loader.getLocation() == null) {
            throw new IllegalStateException("To use load() use constructor WrappedFXMLLoader(String location)");
        }

        try {
            return new FXWrapper<>(loader.load(), loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load FXML", e);
        }
    }


    public <C extends ControllerBase<?>> FXWrapper<C> load(String location) {
        if (loader != null && loader.getLocation() != null) {
            throw new IllegalStateException("To use load(String location) use constructor WrappedFXMLLoader()");
        }

        loader = new FXMLLoader(getClass().getResource(location));

        try {
            FXWrapper<C> wrapper = new FXWrapper<>(loader.load(), loader.getController());
            loader = null;
            return wrapper;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load FXML", e);
        }
    }

}
