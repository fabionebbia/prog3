package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseStatus;

public enum Status implements BaseStatus {

    // Generic
    IDLE(false, ""),
    MALFORMED_EMAIL_ADDRESS(true, "Malformed email address"),

    // Login
    LOGIN_BLANK_SERVER_FIELD(true, "Please insert server address"),
    LOGIN_BLANK_EMAIL_FIELD(true, "Please insert e-mail address"),
    LOGIN_SUCCESS(false, "Successfully logged in");

    private final boolean isError;
    private final String message;

    Status(boolean isError, String message) {
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
