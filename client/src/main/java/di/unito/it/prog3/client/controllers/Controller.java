package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.ControllerBase;

public abstract class Controller extends ControllerBase<Model> {

    /**
     * Called the the view associated with this controller
     * is displayed on parent screen.
     */
    void onDisplayed() {}


    /**
     * Called the the view associated with this controller
     * is removed from parent screen.
     */
    void onHidden() {}

}
