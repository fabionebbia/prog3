package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.model.Error;

public class LoginRequest {

    private final LoginForm form;

    public LoginRequest(LoginForm form) {
        this.form = form;
    }


    public static class LoginResponse implements Response {

        public static final LoginResponse SUCCESS            = new LoginResponse(false, "Successfully logged in");
        public static final LoginResponse ALREADY_LOGGED_IN  = new LoginResponse(true, "Already logged in");
        public static final LoginResponse BLANK_EMAIL        = new LoginResponse(true, "E-mail is required");
        public static final LoginResponse MALFORMED_EMAIL    = new LoginResponse(true, "Malformed e-mail address");
        public static final LoginResponse UNKNOWN_EMAIL      = new LoginResponse(true, "Unknown e-mail address");
        public static final LoginResponse UNREACHABLE_SERVER = new LoginResponse(true, "Cannot reach server");

        private Error error;
        private String message;

        private LoginResponse(boolean isError, String message) {
            if (isError) {
                error = createError(message);
            } else {
                this.message = message;
            }
        }

        @Override
        public boolean isError() {
            return error != null;
        }

        public Error getError() {
            return error;
        }

        @Override
        public String getMessage() {
            return (message != null) ? message : error.getContent();
        }

        private static Error createError(String message) {
            return new Error("Login error", "Could not log in", message);
        }
    }


}
