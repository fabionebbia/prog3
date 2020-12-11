package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.ControllerBase;

public abstract class Controller extends ControllerBase<Model> {

    void onDisplayed() {}

    void onHidden() {}

}
