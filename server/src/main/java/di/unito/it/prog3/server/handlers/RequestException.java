package di.unito.it.prog3.server.handlers;

public class RequestException extends Exception {

    RequestException(String message, Throwable t) {
        super(message, t);
    }

    RequestException(String message) {
        super(message);
    }

}
