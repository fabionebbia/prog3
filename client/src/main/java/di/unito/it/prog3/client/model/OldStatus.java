package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseStatus;

public enum OldStatus implements BaseStatus {

    // Generic
    IDLE(false, "Awaiting login"),
    MALFORMED_EMAIL_ADDRESS(true, "Malformed email address"),

    // Login
    LOGIN_INVALID_SERVER_ADDRESS(true, "Please specify a valid server address"),
    LOGIN_INVALID_SERVER_PORT(true, "Please specify a valid port"),
    LOGIN_INVALID_EMAIL(true, "Please specify a valid e-mail address"),

    LOGIN_SUCCESS(false, "Successfully logged in"),
    LOGIN_FAILURE(true, ""),
    LOGIN_UNKNOWN_USER(true, "User does not exist"),
    LOGIN_CANNOT_CONNECT(true, ""),

    SUCCESS(false, ""),
    INVALID_EMAIL(true, "");

    private final boolean isError;
    private final String message;

    OldStatus(boolean isError, String message) {
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
