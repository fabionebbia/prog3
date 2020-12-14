package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.*;
import di.unito.it.prog3.libs.net.ReadRequest.ReadRequestBuilder;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.ObjectCallback;
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


    Client(Model model, Application.Parameters parameters) {
        Map<String, String> params = parameters.getNamed();
        String serverParam = params.getOrDefault("server", "localhost:9999");
        String userParam = params.get("user");

        String[] serverParts = serverParam.split(":");
        try {
            if (serverParts.length != 2) {
                throw new IllegalArgumentException();
            }
            host = serverParts[0];
            port = Integer.parseInt(serverParts[1]);
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid server address");
        }

        if (Emails.isMalformed(userParam)) {
            throw new IllegalArgumentException("Invalid user e-mail");
        }

        this.model = model;
        this.user = new ReadOnlyStringWrapper(userParam);
        this.server = new ReadOnlyStringWrapper(serverParam);

        json = new JsonMapper();
        status = new ReadOnlyObjectWrapper<>(IDLE);
        firstRequestSent = new ReadOnlyBooleanWrapper(false);

        try {
            pollingInterval = Integer.parseInt(params.getOrDefault("polling-interval", "30"));
            if (pollingInterval <= 0) {
                throw new IllegalArgumentException(); // needed to trigger the catch beloe
            }
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid polling interval");
        }

        executor = Executors.newSingleThreadScheduledExecutor();

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

    void startPoller() {
        if (!pollerStarted) {
            executor.scheduleAtFixedRate(this::poll, 0, pollingInterval, TimeUnit.SECONDS);
            pollerStarted = true;
        }
    }

    private void poll() {
        try {
            ReadRequestBuilder request = newRequest(READ);

            LocalDateTime pivot;

            if (model.allQueue().getValue().isEmpty()) {
                request.setDirection(Chrono.OLDER);
                pivot = LocalDateTime.now();

                DEBUG("Downloading mailbox");
            } else if (model.receivedQueue().getValue().isEmpty()) {
                request.setDirection(Chrono.OLDER);
                request.setQueue(Queue.RECEIVED);
                pivot = LocalDateTime.now();

                DEBUG("Polling whole RECEIVED queue");
            } else {
                Email lastReceived = model.receivedQueue().getValue().get(0);
                pivot = lastReceived.getTimestamp();

                request.setDirection(Chrono.NEWER);
                request.setQueue(Queue.RECEIVED);

                DEBUG("Polling RECEIVED queue after last received e-mail");
            }

            request.setPivot(pivot);

            request.setSuccessHandler(response -> {
                List<Email> newEmails = response.getEmails();
                model.allQueue().addAll(newEmails);

                /*if (!firstRequestSent.get()) {
                    firstRequestSent.set(true);
                }*/
            });

            request.commit();
        } catch (Exception e) {
            Platform.runLater(() -> { throw e; });
        }
    }

    public <R extends Request, B extends RequestBuilder<R>> B newRequest(RequestBuilderSupplier<R, B> type) {
        B builder = type.supply(this::commitRequest);
        builder.setUser(user.get());
        return builder;
    }

    <R extends Request> void commitRequest(RequestBuilder<R> requestBuilder) {
        R request = requestBuilder.getRequest();
        request.validate();

        ResponseHandler onSuccess = requestBuilder.getSuccessHandler();
        ResponseHandler onFailure = requestBuilder.getFailureHandler();

        if (onFailure != null) {
            sendRequest(request, onSuccess, onFailure);
        } else {
            sendRequest(request, onSuccess, response -> {
                throw new RuntimeException(response.getMessage());
            });
        }
    }

    void sendRequest(Request request, ResponseHandler onSuccess, ResponseHandler onFailure) {
        Objects.requireNonNull(request);

        executor.schedule(() -> {
            try (Socket socket = new Socket(host, port);
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // TODO dovel lo metto?
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
                if (!(e instanceof ConnectException)) {
                    e.printStackTrace();
                }
                setStatus(UNREACHABLE_SERVER);
            }
        }, 0, TimeUnit.SECONDS);
    }

    public ReadOnlyObjectProperty<ClientStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty firstRequestSent() {
        return firstRequestSent.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty serverProperty() {
        return server.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty userProperty() {
        return user.getReadOnlyProperty();
    }

    public void shutdown() throws InterruptedException {
        DEBUG("Shutting down executor service");

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);

        DEBUG("Executor service shut down");
    }

    private void setStatus(ClientStatus newStatus) {
        Platform.runLater(() -> {
            DEBUG("Status changed: " + status.get() + " -> " + newStatus);

            status.set(newStatus);
        });
    }

}
