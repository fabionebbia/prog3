package di.unito.it.prog3.server.gui;

import di.unito.it.prog3.libs.net.Response;
import javafx.application.Platform;

public class Logger {

    private final Model model;

    public Logger(Model model) {
        this.model = model;
    }

    public void info(String message) {
        doLog(false, message);
    }

    public void info(LogSession log) {
        doLog(false, log.commit());
    }

    public void error(String message) {
        doLog(true, message);
    }

    public void exception(Throwable t) {
        doLog(true, t.getClass().getSimpleName() + ": " + t.getMessage());
    }

    public void log(Response response) {
        String message = response.getMessage();
        if (response.successful()) {
            info(message);
        } else {
            error(message);
        }
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
