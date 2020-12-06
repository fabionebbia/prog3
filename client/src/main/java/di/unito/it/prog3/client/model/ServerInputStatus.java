package di.unito.it.prog3.client.model;

public class ServerInputStatus extends StatusBase {

    public static final ServerInputStatus CORRECT = new ServerInputStatus(OK, "");
    public static final ServerInputStatus BLANK = new ServerInputStatus(ERR, "Server address required");

    protected ServerInputStatus(boolean isError, String message) {
        super(isError, message);
    }
}
