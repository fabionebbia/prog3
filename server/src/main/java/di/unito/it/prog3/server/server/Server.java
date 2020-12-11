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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends Thread {

    private final Map<Request.Type, RequestHandler> handlers;
    private final EmailStore emailStore;
    private final JsonMapper json;

    private AtomicBoolean shouldContinue;
    private ServerSocket serverSocket;
    private ExecutorService socketAcceptingExecutor;
    private ExecutorService requestHandlingExecutor;
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
        int nAcceptingWorkers, nHandlingWorkers;

        Map<String, String> params = parameters.getNamed();
        String nAcceptingWorkersParam = params.getOrDefault("accepting-workers", "3");
        String nHandlingWorkersParam = params.getOrDefault("handling-workers", "3");
        String portParam = params.getOrDefault("port", "9999");

        try {
            nAcceptingWorkers = Integer.parseInt(nAcceptingWorkersParam);
            nHandlingWorkers = Integer.parseInt(nHandlingWorkersParam);
            port = Integer.parseInt(portParam);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid config params", e);
        }

        logger = new Logger(model);
        shouldContinue = new AtomicBoolean(true);
        socketAcceptingExecutor = Executors.newFixedThreadPool(nAcceptingWorkers);
        requestHandlingExecutor = Executors.newFixedThreadPool(nHandlingWorkers);

        super.start();
    }

    @Override
    public void run() {
        logger.info("Listening for client requests on port " + port);

        try {
            serverSocket = new ServerSocket(port);
            while (shouldContinue.get()) {
                Socket incoming = serverSocket.accept();
                socketAcceptingExecutor.submit(new SocketHandler(incoming));
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
            socketAcceptingExecutor.shutdown();
            socketAcceptingExecutor.awaitTermination(3, TimeUnit.SECONDS);
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
            CompletionService<Response> completionService
                    = new ExecutorCompletionService<>(requestHandlingExecutor);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Request request = json.readValue(br, Request.class);
                Response response = Response.failure("Server error");

                logger.info("Received " + request.getType());

                if (request != null) {
                    Request.Type type = request.getType();
                    RequestHandler handler = handlers.get(type);

                    logger.info("Retrieved handler (" + handler + ")");

                    if (handler != null) {
                        response = handler.execute(completionService, emailStore, logger, request);
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
