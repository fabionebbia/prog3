package di.unito.it.prog3.client.fxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {

    private final Map<ScreenSpec<?>, Screen> screens;
    private final Stage primaryStage;
    private final Class<? extends Application> resourceClass;

    public ScreenManager(Stage primaryStage, Class<? extends Application> resourceClass) {
        this.primaryStage = primaryStage;
        this.resourceClass = resourceClass;
        screens = new HashMap<>();
    }

    public void setScreen(ScreenSpec<?> screenSpec) {
        if (screens.containsKey(screenSpec)) {
            Screen screen = screens.get(screenSpec);
            primaryStage.setScene(screen.getScene());
            primaryStage.setTitle(screen.getTitle());
        } else throw new IllegalStateException("Trying to set a screen which has not yet been loaded");
    }

    public <M> void load(ScreenSpec<M> screenSpec, M model) throws  IOException {
        Screen screen = loadSpec(screenSpec, model);
        if (!screens.containsKey(screenSpec)) {
            screens.put(screenSpec, screen);
        } else throw new IllegalStateException("Trying to load a screen which has already been loaded");
    }

    public <M> void loadAndSet(ScreenSpec<M> screenSpec, M model) throws IOException {
        load(screenSpec, model);
        setScreen(screenSpec);
    }

    public <Q> Parent loadFxml(String fxmlFile, Q model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent screenContent = loader.load();
        Controller<Q> controller = loader.getController();
        controller.init(this, model);
        return screenContent;
    }

    public <M> Screen loadSpec(ScreenSpec<M> screenSpec, M model) throws IOException {
        return screenSpec.getLoader().load(this, model);
    }

    public String loadStylesheet(String path) {
        return getClass().getResource(path).toExternalForm();
    }

    public void show() {
        primaryStage.show();
    }

    // TODO
    public <M> Parent test(String fxmlFile, Class<M> modelClass, M model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

        loader.setControllerFactory((Class<?> controllerClass) -> {
            try {
                for (Constructor<?> c : controllerClass.getConstructors()) {
                    if (c.getParameterCount() == 1 && c.getParameterTypes()[0].equals(modelClass)) {
                        return c.newInstance(model);
                    }
                }

                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        });

        return loader.load();
    }
}
