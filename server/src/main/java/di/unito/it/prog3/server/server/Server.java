package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.requests.*;
import di.unito.it.prog3.server.gui.Model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable {

    private static final JsonMapper json;

    static {
        json = new JsonMapper();
        json.registerSubtypes(
                LoginRequest.class,
                ReadSingleRequest.class,
                ReadMultipleRequest.class,
                StoreRequest.class,
                DeletionRequest.class
        );
    }

    private final AtomicBoolean shouldContinue;
    private final ExecutorService executor;
    private final Model model;
    private final int port;

    private ServerSocket serverSocket;

    public Server(Model model, int port) {
        this.model = model;
        this.port = port;

        shouldContinue = new AtomicBoolean(true);
        executor = Executors.newFixedThreadPool(3);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            while (shouldContinue.get()) {
                Socket incoming = serverSocket.accept();
                executor.submit(new SocketHandler(incoming));
            }
        } catch (SocketException ignored) {
            // shutdown method closed the socket
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void shutdown() throws IOException {
        if (!shouldContinue.get()) {
            throw new IllegalStateException("Server is not running");
        }
        shouldContinue.set(false);
        serverSocket.close();
        executor.shutdown();
    }

    public static JsonMapper getJsonMapper() {
        return json;
    }

}
