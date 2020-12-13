package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Chrono;
import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Response;

import di.unito.it.prog3.libs.net2.*;
import di.unito.it.prog3.libs.net2.ReadRequest.ReadRequestBuilder;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.ValueCallback;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static di.unito.it.prog3.client.model.ClientStatus.*;
import static di.unito.it.prog3.libs.net2.RequestType.LOGIN;
import static di.unito.it.prog3.libs.net2.RequestType.READ;

public class Client {

    private final ReadOnlyObjectWrapper<ClientStatus> status;
    private final ReadOnlyStringWrapper server;
    private final ReadOnlyStringWrapper user;

    private final ScheduledExecutorService executor;
    private final JsonMapper json;
    private final Model model;

    private final long pollingInterval;
    private final String host;
    private final int port;

    private boolean pollerStarted;

    private LocalDateTime lastCheck;


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
        status = new ReadOnlyObjectWrapper<>(IDLE);
        json = new JsonMapper();

        try {
            pollingInterval = Integer.parseInt(params.getOrDefault("polling-interval", "5"));
            if (pollingInterval <= 0) {
                throw new IllegalArgumentException();
            }
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid polling interval");
        }

        executor = Executors.newSingleThreadScheduledExecutor();
    }

    void startPoller() {
        if (!pollerStarted) {
            executor.scheduleAtFixedRate(
                    this::poll,
                    0,
                    pollingInterval,
                    TimeUnit.SECONDS
            );
            pollerStarted = true;
        }
        newRequest(LOGIN)
                .commit();
    }

    private void poll() {
        try {
            ReadRequestBuilder request = newRequest(READ);

            LocalDateTime pivot;

            if (model.allQueue().getValue().isEmpty()) {
                request.setDirection(Chrono.OLDER);
                pivot = LocalDateTime.now();
                System.out.println("A");
            } else if (model.receivedQueue().getValue().isEmpty()) {
                request.setDirection(Chrono.OLDER);
                request.setQueue(Queue.RECEIVED);
                pivot = LocalDateTime.now();
                System.out.println("B");
            } else {
                /*if (lastCheck == null) {
                    Email lastReceived = model.receivedQueue().getValue().get(0);
                    System.out.println("Last received: " + lastReceived.getSubject());
                    pivot = lastReceived.getTimestamp();
                } else {
                    pivot = lastCheck;
                    lastCheck = LocalDateTime.now();
                }*/
                Email lastReceived = model.receivedQueue().getValue().get(0);
                System.out.println("Last received: " + lastReceived.getSubject());
                pivot = lastReceived.getTimestamp();

                request.setDirection(Chrono.NEWER);
                request.setQueue(Queue.RECEIVED);
                System.out.println("C");
            }

            /*request.onSuccess(response -> {
                List<Email> newEmails = response.getEmails();
                model.allQueue().addAll(newEmails);
            });*/

            request.setPivot(pivot);
            System.out.println(request.build().getPivot());

            request.setOnSuccessCallback(response -> {
                List<Email> newEmails = response.getEmails();
                System.out.println(newEmails);
                model.allQueue().addAll(newEmails);
            });

            // request.send();
            request.commit();
        } catch (Exception e) {
            Platform.runLater(() -> { throw e; });
        }

        /*Email lastReceived = model.receivedQueue().getValue().get(0);
        // Email.ID offset = lastReceived.getId();

        LocalDateTime pivot;

        if (lastReceived != null) {
            pivot = lastReceived.getTimestamp();
        } else {
            pivot = LocalDateTime.now();
        }

        try {
            if (queuePopulated) {
                model.loadNewerReceived();
            } else {
                populateQueues();
            }
        } catch (Exception e) {
            Platform.runLater(() -> { throw e; });
        }*/
    }

    public <R extends Request, B extends RequestBuilder<R>> B newRequest(RequestBuilderSupplier<R, B> type) {
        B builder = type.supply(this::commitRequest);
        builder.setUser(user.get());
        return builder;
    }

    <R extends Request> void commitRequest(RequestBuilder<R> requestBuilder) {
        R request = requestBuilder.build();
        request.validate();
        sendRequest(
                request,
                requestBuilder.getOnSuccessCallback(),
                requestBuilder.getOnFailureCallback()
        );
    }

    void sendRequest(Request request, ValueCallback<Response> onSuccess) {
        sendRequest(request, onSuccess, response -> {
            throw new RuntimeException(response.getMessage());
        });
    }

    void sendRequest(Request request, ValueCallback<Response> onSuccess, ValueCallback<Response> onFailure) {
        Objects.requireNonNull(request);

        executor.schedule(() -> {
            try (Socket socket = new Socket(host, port);
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // TODO dovel lo metto?
                socket.setSoTimeout(5000);

                setStatus(CONNECTED);

                System.out.println(json.writeValueAsString(request));
                json.writeValue(socket.getOutputStream(), request);
                Response response = json.readValue(br, Response.class);
                System.out.println(json.writeValueAsString(response));

                Platform.runLater(() -> {
                    if (response.successful()) {
                        if (onSuccess != null) onSuccess.call(response);
                    } else {
                        if (onFailure != null) onFailure.call(response);
                    }
                });

                setStatus(IDLE);
            } catch (Exception e) {
                e.printStackTrace();
                setStatus(UNREACHABLE_SERVER);
            }
        }, 0, TimeUnit.SECONDS);
    }

    /*void sendRequest(Request request) {
        Objects.requireNonNull(request);

        executor.schedule(() -> {
            try (Socket socket = new Socket(host, port);
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                // TODO dovel lo metto?
                socket.setSoTimeout(5000);

                setStatus(CONNECTED);

                System.out.println(json.writeValueAsString(request));
                json.writeValue(socket.getOutputStream(), request);
                Response response = json.readValue(br, Response.class);
                System.out.println(json.writeValueAsString(response));
                Platform.runLater(() -> request.gotResponse(response));

                setStatus(IDLE);
            } catch (Exception e) {
                e.printStackTrace();
                setStatus(UNREACHABLE_SERVER);
            }
        }, 0, TimeUnit.SECONDS);
    }*/

    public ReadOnlyObjectProperty<ClientStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty serverProperty() {
        return server.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty userProperty() {
        return user.getReadOnlyProperty();
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
    }

    private void setStatus(ClientStatus newStatus) {
        Platform.runLater(() -> status.set(newStatus));
    }

}
