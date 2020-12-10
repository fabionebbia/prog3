package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.requests.*;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.handlers.LoginRequestHandler;
import di.unito.it.prog3.server.handlers.RequestException;
import di.unito.it.prog3.server.handlers.RequestHandler;
import di.unito.it.prog3.server.storage.ConcurrentJsonEmailStore;
import di.unito.it.prog3.server.storage.EmailStore;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Thread {

    private final Map<RequestType, RequestHandler> handlers;
    private final EmailStore emailStore;
    private final JsonMapper json;

    private AtomicBoolean shouldContinue;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private Logger logger;
    private int port;


    public Server() {
        super("Server Thread");

        emailStore = new ConcurrentJsonEmailStore("_store");
        json = new JsonMapper();

        handlers = new ConcurrentHashMap<>();
        handlers.put(RequestType.LOGIN, new LoginRequestHandler());
    }

    @Override
    public synchronized void start() {
        throw new UnsupportedOperationException("Use start(Model, Parameters) instead");
    }

    public synchronized void start(Model model, Application.Parameters parameters) {
        Map<String, String> params = parameters.getNamed();
        port = Integer.parseInt(params.getOrDefault("port", "2525"));
        int nWorkers = Integer.parseInt(params.getOrDefault("n-workers", "3"));

        logger = new Logger(model);
        shouldContinue = new AtomicBoolean(true);
        executor = Executors.newFixedThreadPool(nWorkers);

        super.start();
    }

    @Override
    public void run() {
        logger.info("Listening for client requests on port " + port);

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
            logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public synchronized void shutdown() {
        try {
            shouldContinue.set(false);
            serverSocket.close();
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to close server socket");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Unable to shutdown executor service");
        }
    }

    private class SocketHandler implements Runnable {

        private final Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Request request = json.readValue(bufferedReader, Request.class);

                RequestType type = request.getType();
                RequestHandler handler = handlers.get(type);
                Response response = handler.execute(emailStore, request);
                
                json.writeValue(socket.getOutputStream(), response);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
