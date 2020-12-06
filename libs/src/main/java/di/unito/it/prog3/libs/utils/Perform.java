package di.unito.it.prog3.libs.utils;

import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;

public final class Perform {

    private static ExecutorService executor;

    public static void setExecutor(ExecutorService executor) {
        Perform.executor = executor;
    }

    public static void async(Request request, Callback callback) {
        executor.submit(() -> {
            request.perform();
            Platform.runLater(callback::call);
        });
    }

    public static <T> void async(Callback.TypedRequest<T> request, Callback.TypedCallback<T> callback) {
        executor.submit(() -> {
            T response = request.perform();
            Platform.runLater(() -> callback.call(response));
        });
    }

    public static <I, R> void async(I input, Callback.RequestWithInput<I, R> request, Callback.TypedCallback<R> callback) {
        executor.submit(() -> {
            R response = request.perform(input);
            Platform.runLater(() -> callback.call(response));
        });
    }

    public interface Request {
        void perform();
    }

}
