package di.unito.it.prog3.client.fxml;

public interface Controller<T> {

    void init(ScreenManager screenManager, T model);

}
