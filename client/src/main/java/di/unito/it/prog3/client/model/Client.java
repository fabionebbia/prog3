package di.unito.it.prog3.client.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import di.unito.it.prog3.libs.communication.net.requests.DeletionRequest;
import di.unito.it.prog3.libs.communication.net.requests.LoginRequest;
import di.unito.it.prog3.libs.communication.net.requests.Request;
import di.unito.it.prog3.libs.communication.net.requests.TestConnection;
import di.unito.it.prog3.libs.communication.net.responses.Response;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.store.EmailStore;
import di.unito.it.prog3.libs.store.EmailStoreException;
import di.unito.it.prog3.libs.store.Queue;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

import static di.unito.it.prog3.client.model.ClientStatus.IDLE;
import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

class Client {

    private final ReadOnlyObjectWrapper<ClientStatus> status;

    private final Model model;

    private String host;
    private int port;
    private String user;

    private final ObjectMapper json;

    public Client(Model model) {
        this.model = model;

        status = new ReadOnlyObjectWrapper<>(IDLE);
        json = new ObjectMapper();
    }

    public Response login(String host, int port, String user) {
        this.host = host;
        this.port = port;
        this.user = user;

        return sendRequest(new LoginRequest(null /* TODO */));
    }

    public Response sendRequest(Request request) {
        Objects.requireNonNull(request);

        request.setUser(user);

        try (Socket socket = new Socket(host, port)) {
            // ok mapper.writeValue(socket.getOutputStream(), new LoginRequest("a@b.c"));
            // ok mapper.writeValue(socket.getOutputStream(), new ReadRequest.ReadRequestSingle(Email.ID.fromString("a@b.c/R/0b94f5c6-bf38-4048-890d-72588641a405")));
            // ok mapper.writeValue(socket.getOutputStream(), new ReadRequest.ReadRequestMany(Email.ID.fromString("a@b.c/R/0b94f5c6-bf38-4048-890d-72588641a405"), 5));
            json.writeValue(socket.getOutputStream(), request);
            return json.readValue(socket.getInputStream(), Response.class);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid server address");
        } catch (IOException e) {
            Platform.runLater(() -> status.set(UNREACHABLE_SERVER));
            throw new RuntimeException("Cannot reach server");
        }
    }

    protected ReadOnlyObjectProperty<ClientStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

}
