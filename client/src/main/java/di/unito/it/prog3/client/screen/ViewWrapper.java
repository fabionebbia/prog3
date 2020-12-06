package di.unito.it.prog3.client.screen;

import javafx.scene.Node;

class ViewWrapper {

    private final Controller controller;
    private final Node node;

    ViewWrapper(Node node, Controller controller) {
        this.controller = controller;
        this.node = node;
    }

    Node getNode() {
        return node;
    }

    Controller getController() {
        return controller;
    }
}