package di.unito.it.prog3.server.storage;

public class EmailStoreException extends Exception {

    public EmailStoreException(String message, Throwable t) {
        super(message, t);
    }

    public EmailStoreException(String message) {
        super(message);
    }

}
