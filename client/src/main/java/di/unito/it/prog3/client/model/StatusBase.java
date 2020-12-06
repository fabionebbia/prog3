package di.unito.it.prog3.client.model;

public class StatusBase implements Status {

    private final boolean isError;
    private final String message;

    protected StatusBase(boolean isError, String message) {
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
