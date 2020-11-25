package di.unito.it.prog3.libs.store;

public class EmailStoreException extends Exception {

    public EmailStoreException(String message) {
        super(message);
    }

    public static class NonExistingUserException extends EmailStoreException {
        public NonExistingUserException(String user) {
            super("Non-existing user " + user);
        }
    }
}
