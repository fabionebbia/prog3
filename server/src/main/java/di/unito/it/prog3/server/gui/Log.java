package di.unito.it.prog3.server.gui;

public final class Log {

    private final boolean isError;
    private final String message;

    Log(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public String getMessage() {
        return message;
    }

}
