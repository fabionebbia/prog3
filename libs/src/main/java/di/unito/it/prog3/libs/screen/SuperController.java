package di.unito.it.prog3.libs.screen;

import javafx.scene.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class SuperController<M> extends Controller<M> {

    private final Map<String, ViewWrapper<M>> views = new HashMap<>();
    private String defaultView;
    private String root;


    protected String getRoot() {
        return root;
    }

    protected void setRoot(String root) {
        this.root = root;
    }

    protected void setView(String view) {
        ViewWrapper<M> wrapper = views.get(view);
        setView(wrapper.getNode());
    }

    protected void loadView(String view) {
        if (views.containsKey(view)) {
            throw new IllegalStateException("View " + view + " already loaded");
        }
        try {
            String resource = "/screens/" + getRoot() + "/" + view + ".fxml";
            LoadedFX<M,Node, Controller<M>> fx = screenManager.loadFXML(resource);

            Controller<M> controller = fx.getController();
            controller.init(screenManager, model);
            Node content = fx.getContent();

            setView(content);
            views.put(view, new ViewWrapper<>(content, controller));
        } catch (IOException e) {
            throw new RuntimeException("Could not load view " + view, e);
        }
    }

    protected void setView(Node view) {
        throw new UnsupportedOperationException(root + " scene does not support subviews");
    }

}
