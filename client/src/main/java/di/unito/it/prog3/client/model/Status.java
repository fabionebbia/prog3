package di.unito.it.prog3.client.model;

public interface Status {

    boolean isError();

    String getMessage();

    boolean OK = false;
    boolean ERR = true;
}
