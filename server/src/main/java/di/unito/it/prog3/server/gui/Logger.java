package di.unito.it.prog3.server.gui;

import di.unito.it.prog3.libs.net.Response;
import javafx.application.Platform;

public class Logger {

    private final Model model;

    public Logger(Model model) {
        this.model = model;
    }


    /**
     * Logs a message.
     *
     * @param message The message that must be logged.
     */
    public void info(String message) {
        doLog(false, message);
    }


    /**
     * Logs a whole log session.
     *
     * @param log The log session that must be logged.
     */
    public void info(LogSession log) {
        doLog(false, log.commit());
    }


    /**
     * Logs a message as an error.
     *
     * @param message The error message.
     */
    public void error(String message) {
        doLog(true, message);
    }


    /**
     * Logs an exception.
     *
     * @param t The exception that must be logged.
     */
    public void exception(Throwable t) {
        doLog(true, t.getClass().getSimpleName() + ": " + t.getMessage());
    }


    /**
     * Logs a response.
     *
     * @param response The response that must be logged.
     */
    public void log(Response response) {
        String message = response.getMessage();
        if (response.successful()) {
            info(message);
        } else {
            error(message);
        }
    }


    /**
     * Adds a new log to the logs property in the model.
     *
     * @param isError Flag that indicates whether the new log represents an error or not.
     * @param message The message of the new log.
     */
    private void doLog(boolean isError, String message) {
        Log newLog = new Log(isError, message);
        Platform.runLater(() -> {
            synchronized (model.logsProperty()) {
                model.logsProperty().add(newLog);
            }
        });
    }
}
