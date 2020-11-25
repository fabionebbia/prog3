package di.unito.it.prog3.libs.exceptions;

public class MalformedEmailIDException extends IllegalArgumentException {

    public MalformedEmailIDException(String id) {
        super("Malformed email ID " + id);
    }

}
