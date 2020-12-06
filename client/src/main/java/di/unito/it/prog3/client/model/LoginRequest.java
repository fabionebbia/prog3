package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseStatus;
import di.unito.it.prog3.client.model.requests.Response;
import di.unito.it.prog3.libs.model.Error;

public class LoginRequest {

    public enum LoginResponse implements Response, BaseStatus {
        SUCCESS(false, "Successfully logged in"),
        ALREADY_LOGGED_IN(true, "Already logged in"),
        BLANK_EMAIL(true, "E-mail is required"),
        MALFORMED_EMAIL(true, "Malformed e-mail address"),
        UNKNOWN_EMAIL(true, "Unknown e-mail address"),
        UNREACHABLE_SERVER(true, "Cannot reach server");

        private final boolean isError;
        private final String message;

        LoginResponse(boolean isError, String message) {
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

        @Override
        public Error toError() {
            return new Error("Error", "Could not login in", message);
        }
    }


}
