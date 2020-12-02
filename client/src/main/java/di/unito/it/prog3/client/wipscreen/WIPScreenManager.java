package di.unito.it.prog3.client.wipscreen;

import di.unito.it.prog3.client.model.Model;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class WIPScreenManager {

    private final Map<Class<? extends WIPScreen>, WIPScreen> screens;
    private final Stage primaryStage;

    public WIPScreenManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        screens = new HashMap<>();
    }

    protected void setScreen(WIPScreen screen) throws IOException {
        primaryStage.setScene(screen.getScene());
        primaryStage.setTitle(screen.getTitle());
    }

    public void setScreen(Class<? extends WIPScreen> screenClass, Model model) throws IOException {
        screens.putIfAbsent(screenClass, load(screenClass, model));
        setScreen(screens.get(screenClass));
    }

    private WIPScreen load(Class<? extends WIPScreen> screenClass, Model model) {
        try {
            WIPScreen screen = screenClass.getDeclaredConstructor().newInstance();
            screen.initControl(this, model, false);
            return screen;
        } catch (InstantiationException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e); // TODO
        }
    }

    public <T> T loadFXML(String fxmlFile, WIPScreen screen) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setController(screen);
        T tmp =  loader.load();
        return tmp;
    }

    public <T> T loadFXML(String fxmlFile, Model model) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        T loaded = loader.load();
        WIPController controller = loader.getController();
        controller.initControl(this, model, true);
        return loaded;
    }

    public void show() {
        primaryStage.show();
    }

}
