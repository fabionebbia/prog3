package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseStatus;

public enum ClientStatus implements BaseStatus {

    IDLE(false, ""),
    CONNECTED(false, "Connected"),
    UNREACHABLE_SERVER(true, "Unreachable server")
    ;

    private final boolean isError;
    private final String message;

    ClientStatus(Boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
