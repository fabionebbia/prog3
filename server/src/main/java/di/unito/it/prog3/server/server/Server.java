package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net2.*;
import di.unito.it.prog3.server.gui.Logger;
import di.unito.it.prog3.server.gui.Model;
import di.unito.it.prog3.server.handlers.*;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable {

    // private final Map<Request.Type, RequestHandler> handlers;
    private final Map<Class<? extends Request>, RequestHandler<?>> handlers;
    private final EmailStore emailStore;
    private final JsonMapper json;

    private final AtomicBoolean shouldContinue;
    private final ExecutorService executor;
    private final Logger logger;
    private final int port;

    private ServerSocket serverSocket;

    public Server(Model model, Application.Parameters parameters) {
        json = new JsonMapper();
        emailStore = new ConcurrentJsonEmailStore("_store");

        /*handlers = new ConcurrentHashMap<>();
        handlers.put(Request.Type.LOGIN, new LoginRequestHandler());
        handlers.put(Request.Type.SEND, new SendRequestHandler());
        handlers.put(Request.Type.READ, new ReadRequestHandler());
        handlers.put(Request.Type.OPEN, new OpenRequestHandler());
        handlers.put(Request.Type.DELETE, new DeletionRequestHandler());*/

        handlers = new ConcurrentHashMap<>();
        registerRequestHandler(DeletionRequest.class, new DeletionRequestHandler());
        registerRequestHandler(LoginRequest.class, new LoginRequestHandler());
        registerRequestHandler(ReadRequest.class, new ReadRequestHandler());
        registerRequestHandler(SendRequest.class, new SendRequestHandler());
        registerRequestHandler(OpenRequest.class, new OpenRequestHandler());

        int nWorkers;

        Map<String, String> params = parameters.getNamed();
        String nWorkersParam = params.getOrDefault("accepting-workers", "3");
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
    }

    private <R extends Request> void registerRequestHandler(Class<R> requestClass,
                                                            RequestHandler<R> requestHandler) {
        handlers.put(requestClass, requestHandler);
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
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Request request = json.readValue(br, Request.class);
                Response response = Response.failure("Server error");

                logger.info("Received " + request.getClass().getSimpleName() + " from " + request.getUser());

                //Request.Type type = request.getType();
                Class<? extends Request> requestClass = request.getClass();
                RequestHandler<?> handler = handlers.get(requestClass);

                logger.info("Retrieved handler (" + handler + ")");

                if (handler != null) {
                    response = handler.execute(emailStore, logger, request);
                } else {
                    response = Response.failure("Unknown request type");
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
