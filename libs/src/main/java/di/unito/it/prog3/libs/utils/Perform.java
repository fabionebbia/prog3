package di.unito.it.prog3.libs.utils;

import di.unito.it.prog3.libs.communication.Request;
import di.unito.it.prog3.libs.communication.RequestCallback;
import di.unito.it.prog3.libs.communication.net.responses.Response;
import javafx.application.Platform;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public final class Perform {

    private static ExecutorService executor;

    public static void init(ExecutorService executor) {
        Perform.executor = executor;
    }

    private static void submit(Action action) {
        Objects.requireNonNull(executor);
        executor.submit(action::perform);
    }

    public static void async(Action action) {
        submit(action);
    }

    public static void async(Action action, Callback callback) {
        submit(() -> {
            action.perform();
            Platform.runLater(callback::call);
        });
    }

    public static <T> void async(Request<T> request, RequestCallback<T> callback) {
        submit(() -> {
            try {
                T response = request.perform();
                Platform.runLater(() -> callback.onResponse(response));
            } catch (Exception e) {
                Platform.runLater(() -> callback.onError(e));
            }
        });
    }

}
