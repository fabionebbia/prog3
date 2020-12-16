package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.*;
import di.unito.it.prog3.libs.net.ReadRequest.ReadRequestBuilder;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static di.unito.it.prog3.client.model.ClientStatus.*;
import static di.unito.it.prog3.libs.net.RequestType.READ;
import static di.unito.it.prog3.libs.utils.Utils.DEBUG;

public class Client {

    private final ReadOnlyObjectWrapper<ClientStatus> status;
    private final ReadOnlyBooleanWrapper firstRequestSent;
    private final ReadOnlyStringWrapper server;
    private final ReadOnlyStringWrapper user;

    private final ScheduledExecutorService executor;
    private final JsonMapper json;
    private final Model model;

    private final long pollingInterval;
    private final String host;
    private final int port;

    private boolean pollerStarted;


    /**
     * Validates parameters, sets client properties and created the executor service.
     *
     * @param model The application model.
     * @param parameters The application parameters.
     */
    Client(Model model, Application.Parameters parameters) {
        // Retrieves server address and the user parameters
        Map<String, String> params = parameters.getNamed();
        String serverParam = params.getOrDefault("server", "localhost:9999");
        String userParam = params.get("user");

        // Validates the parameters format and set them in the client
        try {
            String[] serverParts = serverParam.split(":");
            if (serverParts.length != 2) {
                throw new IllegalArgumentException();
            }
            host = serverParts[0];
            port = Integer.parseInt(serverParts[1]);
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid server address");
        }

        // Check che user e-mails address is well formed
        if (Emails.isMalformed(userParam)) {
            throw new IllegalArgumentException("Invalid user e-mail");
        }

        // Checks the polling interval parameter
        try {
            pollingInterval = Integer.parseInt(params.getOrDefault("polling-interval", "30"));
            if (pollingInterval <= 0) {
                throw new IllegalArgumentException(); // needed to trigger the catch beloe
            }
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid polling interval");
        }


        this.model = model;
        this.user = new ReadOnlyStringWrapper(userParam);
        this.server = new ReadOnlyStringWrapper(serverParam);

        json = new JsonMapper();
        status = new ReadOnlyObjectWrapper<>(IDLE);
        firstRequestSent = new ReadOnlyBooleanWrapper(false);

        // Creates the executor service responsible of handling client-server
        // communication and perform the polling for new received e-mails
        executor = Executors.newSingleThreadScheduledExecutor();

        // Listener for client status changes that sets the `firstRequestSent`
        // flag after the initial mailbox content is retrieved, then removes itself
        status.addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends ClientStatus> observable,
                                ClientStatus oldStatus,
                                ClientStatus newStatus) {
                if (oldStatus == CONNECTED && newStatus == IDLE) {
                    firstRequestSent.set(true);
                    status.removeListener(this);
                }
            }
        });
    }


    /**
     * Starts the poller, responsible for retrieving updates from the server.
     */
    void startPoller() {
        if (!pollerStarted) {
            executor.scheduleAtFixedRate(this::poll, 0, pollingInterval, TimeUnit.SECONDS);
            pollerStarted = true;
        }
    }


    /**
     * Internal polling logic based on application status.
     */
    private void poll() {
        try {
            ReadRequestBuilder request = newRequest(READ);

            LocalDateTime pivot;

            if (model.allQueue().getValue().isEmpty()) {
                // If no e-mails was received yet, initialize the mailbox
                request.setDirection(Chrono.OLDER);
                pivot = LocalDateTime.now();

                DEBUG("Downloading mailbox");
            } else if (model.receivedQueue().isEmpty()) {
                // If some e-mail was received but none in the received
                // queue, populate the received queue
                request.setDirection(Chrono.OLDER);
                request.setQueue(Queue.RECEIVED);
                pivot = LocalDateTime.now();

                DEBUG("Polling whole RECEIVED queue");
            } else {
                // If the received queues is already populated, poll updates only
                Email lastReceived = model.receivedQueue().get(0);
                pivot = lastReceived.getTimestamp();

                request.setDirection(Chrono.NEWER);
                request.setQueue(Queue.RECEIVED);

                DEBUG("Polling RECEIVED queue after last received e-mail");
            }

            request.setPivot(pivot);

            // On response success, add any new e-mail the the `all` queue
            // so that filtered ordered sub-queues are automatically updated
            request.setSuccessHandler(response -> {
                List<Email> newEmails = response.getEmails();
                model.allQueue().addAll(newEmails);
            });

            request.commit();
        } catch (Exception e) {
            Platform.runLater(() -> { throw e; });
        }
    }


    /**
     * Supplies a new request builder for the desired request type.
     * Also, sets the user to the currently logged in user.
     *
     * @param type The request builder supplier that represents the type.
     * @param <R> The type of the request that the builder will build.
     * @param <B> The type of the request builder.
     * @return
     */
    public <R extends Request, B extends RequestBuilder<R>> B newRequest(RequestBuilderSupplier<R, B> type) {
        B builder = type.supply(this::commitRequest);
        builder.setUser(user.get());
        return builder;
    }


    /**
     * Commits a request builder, extracts the built request, validates it and sends it.
     *
     * @param requestBuilder The request builder that must be committed.
     * @param <R> The generated request.
     */
    <R extends Request> void commitRequest(RequestBuilder<R> requestBuilder) {
        R request = requestBuilder.getRequest();

        request.validate();

        ResponseHandler onSuccess = requestBuilder.getSuccessHandler();
        ResponseHandler onFailure = requestBuilder.getFailureHandler();

        if (onFailure != null) {
            sendRequest(request, onSuccess, onFailure);
        } else {
            // If no failure handler was specified, use a default one
            // AKA throw a RuntimeException so that JavaFX can display
            // the error to the user
            sendRequest(request, onSuccess, response -> {
                throw new RuntimeException(response.getMessage());
            });
        }
    }


    /**
     * Sends the request to the server, wait for the response and call the associated callbacks.
     *
     * @param request The request that must be sent.
     * @param onSuccess The success callback.
     * @param onFailure The failure callback.
     */
    void sendRequest(Request request, ResponseHandler onSuccess, ResponseHandler onFailure) {
        Objects.requireNonNull(request);

        executor.schedule(() -> {
            try (Socket socket = new Socket(host, port);
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                socket.setSoTimeout(5000);

                setStatus(CONNECTED);

                DEBUG("Request: " + json.writeValueAsString(request));

                json.writeValue(socket.getOutputStream(), request);
                Response response = json.readValue(br, Response.class);

                DEBUG("Response: " + json.writeValueAsString(response));

                Platform.runLater(() -> {
                    if (response.successful()) {
                        if (onSuccess != null) onSuccess.call(response);
                    } else {
                        if (onFailure != null) onFailure.call(response);
                    }
                });

                setStatus(IDLE);
            } catch (Exception e) {
                // Ignore ConnectedException stacktrace as it's displayed
                // by the client based on client status
                if (!(e instanceof ConnectException)) {
                    e.printStackTrace();
                }
                setStatus(UNREACHABLE_SERVER);
            }
        }, 0, TimeUnit.SECONDS);
    }


    /**
     * Exposes the client status property.
     *
     * @return The client status property.
     */
    public ReadOnlyObjectProperty<ClientStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }


    /**
     * Lets the controller know if the first contact with the server has happened.
     *
     * @return The boolean property containing this information.
     */
    public ReadOnlyBooleanProperty firstRequestSent() {
        return firstRequestSent.getReadOnlyProperty();
    }


    /**
     * Exposes the server address property.
     *
     * @return The server adddress property.
     */
    public ReadOnlyStringProperty serverProperty() {
        return server.getReadOnlyProperty();
    }


    /**
     * Exposes the user e-mail address property.
     *
     * @return The user e-mail address property
     */
    public ReadOnlyStringProperty userProperty() {
        return user.getReadOnlyProperty();
    }


    /**
     * Starts an orderly shutdown process of the client.
     */
    public void shutdown() throws InterruptedException {
        DEBUG("Shutting down executor service");

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);

        DEBUG("Executor service shut down");
    }


    /**
     * Sets the new client status.
     *
     * @param newStatus The new status that must be set.
     */
    private void setStatus(ClientStatus newStatus) {
        Platform.runLater(() -> {
            DEBUG("Status changed: " + status.get() + " -> " + newStatus);

            status.set(newStatus);
        });
    }

}
