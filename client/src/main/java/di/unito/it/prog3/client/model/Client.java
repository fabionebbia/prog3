package di.unito.it.prog3.client.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import di.unito.it.prog3.libs.communication.net.JsonMapper;
import di.unito.it.prog3.libs.communication.net.requests.LoginRequest;
import di.unito.it.prog3.libs.communication.net.requests.Request;
import di.unito.it.prog3.libs.communication.net.responses.Response;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import static di.unito.it.prog3.client.model.ClientStatus.IDLE;
import static di.unito.it.prog3.client.model.ClientStatus.UNREACHABLE_SERVER;

class Client {

    private final ReadOnlyObjectWrapper<ClientStatus> status;

    private final Model model;

    private String host;
    private int port;
    private String user;

    private final JsonMapper json;

    public Client(Model model) {
        this.model = model;

        status = new ReadOnlyObjectWrapper<>(IDLE);
        json = new JsonMapper();
    }

    public Response login(String host, int port, String user) {
        this.host = host;
        this.port = port;
        this.user = user;

        return sendRequest(new LoginRequest(null /* TODO */));
    }

    public Response sendRequest(Request request) {
        Objects.requireNonNull(request, "Empty request");

        request.setUser(user);

        try (
                Socket socket = new Socket(host, port);
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            json.writeValue(socket.getOutputStream(), request);
            return json.readValue(bufferedReader, Response.class);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid server address");
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> status.set(UNREACHABLE_SERVER));
            throw new RuntimeException("Cannot reach server");
        }
    }

    protected ReadOnlyObjectProperty<ClientStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

}
