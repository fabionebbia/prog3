package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.handlers.LoginRequestHandler;
import di.unito.it.prog3.server.handlers.RequestHandler;
import di.unito.it.prog3.server.handlers.SendRequestHandler;
import di.unito.it.prog3.server.storage.ConcurrentJsonEmailStore;
import di.unito.it.prog3.server.storage.EmailStore;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Thread {

    private final Map<Request.Type, RequestHandler> handlers;
    private final EmailStore emailStore;
    private final JsonMapper json;

    private AtomicBoolean shouldContinue;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private Logger logger;
    private int port;

    public Server() {
        super("Server Thread");

        json = new JsonMapper();
        emailStore = new ConcurrentJsonEmailStore("_store");

        handlers = new ConcurrentHashMap<>();
        handlers.put(Request.Type.LOGIN, new LoginRequestHandler());
        handlers.put(Request.Type.SEND, new SendRequestHandler());
    }

    public synchronized void start(Model model, Application.Parameters parameters) {
        int nWorkers;

        Map<String, String> params = parameters.getNamed();
        String nWorkersParam = params.getOrDefault("n-workers", "3");
        String portParam = params.getOrDefault("port", "9999");

        try {
            nWorkers = Integer.parseInt(nWorkersParam);
            port = Integer.parseInt(portParam);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid config params", e);
        }

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

    @Override
    public synchronized void start() {
        throw new UnsupportedOperationException("Use start(Model, Parameters) instead");
    }


    private class SocketHandler implements Runnable {

        private final Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Request request = json.readValue(br, Request.class);
                Response response = Response.failure("Server error");

                logger.info("Received " + request.getType());

                if (request != null) {
                    Request.Type type = request.getType();
                    RequestHandler handler = handlers.get(type);

                    logger.info("Retrieved handler (" + handler + ")");

                    if (handler != null) {
                        response = handler.execute(emailStore, logger, request);
                    } else {
                        response = Response.failure("Unknown request type");
                    }
                }

                json.writeValue(socket.getOutputStream(), response);
            } catch (Exception e) {
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
