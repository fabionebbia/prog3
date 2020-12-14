package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.net.*;
import di.unito.it.prog3.server.gui.LogSession;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server implements Runnable {

    private final ConcurrentMap<Class<? extends Request>, RequestHandler<?>> handlers;
    private final EmailStore emailStore;
    private final JsonMapper json;

    private AtomicBoolean shouldContinue;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private Logger logger;
    private int port;


    public Server() {
        json = new JsonMapper();

        // Create the e-mail store
        emailStore = new ConcurrentJsonEmailStore("_store");

        // Register request handlers
        handlers = new ConcurrentHashMap<>();
        registerRequestHandler(DeletionRequest.class, new DeletionRequestHandler());
        registerRequestHandler(LoginRequest.class, new LoginRequestHandler());
        registerRequestHandler(ReadRequest.class, new ReadRequestHandler());
        registerRequestHandler(SendRequest.class, new SendRequestHandler());
        registerRequestHandler(OpenRequest.class, new OpenRequestHandler());
    }


    /**
     * Initializes the server bases on application parameters.
     *
     * @param model The application model.
     * @param parameters The application parameters.
     */
    public void init(Model model, Application.Parameters parameters) {
        int nWorkers;

        // Read parameters or set defaults
        Map<String, String> params = parameters.getNamed();
        String nWorkersParam = params.getOrDefault("n-workers", "3");
        String portParam = params.getOrDefault("port", "9999");

        // Validates parameters
        try {
            nWorkers = Integer.parseInt(nWorkersParam);
            port = Integer.parseInt(portParam);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid config params", e);
        }

        logger = new Logger(model);
        shouldContinue = new AtomicBoolean(true);

        // Initialize the executor service
        executor = Executors.newFixedThreadPool(nWorkers);
    }


    /**
     * Registers an new request handler.
     *
     * @param requestClass The class (type) of request the handler handles.
     * @param requestHandler The request handler.
     * @param <R> The generic type of the request.
     */
    private <R extends Request> void registerRequestHandler(Class<R> requestClass,
                                                            RequestHandler<R> requestHandler) {
        handlers.put(requestClass, requestHandler);
    }


    /**
     * Accepts incoming requests and delegate their handling to a new socket handler.
     */
    @Override
    public void run() {
        logger.info("Server started");
        logger.info("Listening for client requests on port " + port);

        try {
            serverSocket = new ServerSocket(port);
            while (shouldContinue.get()) {
                Socket incoming = serverSocket.accept();
                executor.submit(new SocketHandler(incoming));
            }
        } catch (SocketException ignored) {
            // Shutdown method closed the socket
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Server error..");
            logger.exception(e);
        }
    }


    /**
     * Performs an orderly server shutdown process.
     */
    public synchronized void shutdown() {
        try {
            shouldContinue.set(false);
            serverSocket.close();
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Unable to close server socket");
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("Unable to shutdown executor service");
        }
    }


    /**
     * Socket handler responsible for handling a single specific request
     * and sending the correspondent response to the client.
     */
    private class SocketHandler implements Runnable {

        private final Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }


        /**
         * Deserializes the request from the socket, handles it
         * and serialize the generated response back to the socket.
         */
        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Request request = json.readValue(br, Request.class);

                LogSession log = new LogSession(request);

                Class<? extends Request> requestClass = request.getClass();
                RequestHandler<?> handler = handlers.get(requestClass);

                log.appendln("Retrieved handler (" + handler.getClass().getSimpleName() + "), processing request..");

                Response response = handler.execute(emailStore, log, request);

                log.appendln(response);

                json.writeValue(socket.getOutputStream(), response);

                logger.info(log);
            } catch (Exception e) {
                e.printStackTrace();
                logger.exception(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.exception(e);
                }
            }
        }
    }
}
