package di.unito.it.prog3.libs.screen;

import javafx.scene.Node;

class ViewWrapper<M> {

    private final Controller<M> controller;
    private final Node node;

    ViewWrapper(Node node, Controller<M> controller) {
        this.controller = controller;
        this.node = node;
    }

    Node getNode() {
        return node;
    }

    Controller<M> getController() {
        return controller;
    }
}