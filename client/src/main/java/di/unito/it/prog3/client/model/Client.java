package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Request;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static di.unito.it.prog3.client.model.ClientStatus.*;

public class Client {

    private final ReadOnlyObjectWrapper<ClientStatus> status;
    private final ReadOnlyStringWrapper server;
    private final ReadOnlyStringWrapper user;

    private final ScheduledExecutorService poller;
    private final ExecutorService executor;
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
        status = new ReadOnlyObjectWrapper<>(IDLE);
        json = new JsonMapper();

        executor = Executors.newSingleThreadExecutor();

        try {
            pollingInterval = Integer.parseInt(params.getOrDefault("polling-interval", "5"));
            if (pollingInterval <= 0) {
                throw new IllegalArgumentException();
            }
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Invalid polling interval");
        }

        poller = Executors.newSingleThreadScheduledExecutor();
    }

    void startPoller() {
        if (!pollerStarted) {
            poller.scheduleAtFixedRate(
                    model::loadNewerReceived,
                    pollingInterval,
                    pollingInterval,
                    TimeUnit.SECONDS
            );
        }
        pollerStarted = true;
    }

    public Request.RequestBuilder newRequest(Request.Type type) {
        return new Request.RequestBuilder(type, user.get(), this::sendRequest);
    }

    void sendRequest(Request request) {
        Objects.requireNonNull(request);

        executor.submit(() -> {
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
        });
    }

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
        poller.shutdown();
        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);
        poller.awaitTermination(3, TimeUnit.SECONDS);
    }

    private void setStatus(ClientStatus newStatus) {
        Platform.runLater(() -> status.set(newStatus));
    }

}
