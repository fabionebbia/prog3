package di.unito.it.prog3.server.gui;

import javafx.application.Platform;

public class Logger {

    private final Model model;

    public Logger(Model model) {
        this.model = model;
    }

    public void info(String message) {
        doLog(false, message);
    }

    public void error(String message) {
        doLog(true, message);
    }

    private void doLog(boolean isError, String message) {
        Log newLog = new Log(isError, message);
        Platform.runLater(() -> {
            synchronized (model.logsProperty()) {
                model.logsProperty().add(newLog);
            }
        });
    }
}
