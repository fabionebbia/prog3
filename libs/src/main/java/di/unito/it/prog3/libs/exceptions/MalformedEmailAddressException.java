package di.unito.it.prog3.libs.exceptions;

public class MalformedEmailAddressException extends RuntimeException {

    public MalformedEmailAddressException(String address) {
        super("Malformed email address " + address);
    }
}
