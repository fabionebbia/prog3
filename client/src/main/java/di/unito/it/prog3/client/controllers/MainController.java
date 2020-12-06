package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.screen.Controller;

public class MainController extends Controller {

    @Override
    protected void setupControl() {
        screenManager.setView("list-view");
    }

}
