package di.unito.it.prog3.client.model;

import di.unito.it.prog3.client.fxml.model.BaseModel;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

import static di.unito.it.prog3.client.model.ClientStatus.CONNECTED;
import static di.unito.it.prog3.client.model.ClientStatus.IDLE;

public class Client extends BaseModel<ClientStatus> {

    private Timer timer;

    public Client() {
        super(IDLE);
        timer = new Timer();
    }

    public void start() {
        TimerTask blinkTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (getStatus() == CONNECTED)
                        setStatus(IDLE);
                    else setStatus(CONNECTED);
                });
            }
        };
       timer.schedule(blinkTask, 0, 2000);
    }

    public void stop() {
        timer.cancel();
    }
}
