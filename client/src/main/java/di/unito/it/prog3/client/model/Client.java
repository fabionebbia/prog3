package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.requests.Request;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import static di.unito.it.prog3.client.model.ClientStatus.*;

public class Client {

    private final ReadOnlyObjectWrapper<ClientStatus> status;

    private final Model model;

    private String host;
    private int port;
    private String user;

    private final JsonMapper json;

    Client(Model model) {
        this.model = model;

        status = new ReadOnlyObjectWrapper<>(IDLE);
        json = new JsonMapper();
    }

    Response login(String host, int port, String user) {
        this.host = host;
        this.port = port;
        this.user = user;

        return null; // sendRequest(new LoginRequest(null /* TODO */));
    }

     Response sendRequest(Request request) {
        Objects.requireNonNull(request, "Empty request");

        request.setUser(user);

        try (
                Socket socket = new Socket(host, port);
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            Platform.runLater(() -> status.set(CONNECTED));

            json.writeValue(socket.getOutputStream(), request);
            Response response = json.readValue(bufferedReader, Response.class);

            Platform.runLater(() -> status.set(IDLE));

            return response;
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid server address");
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> status.set(UNREACHABLE_SERVER));
            throw new RuntimeException("Cannot reach server");
        }
    }

    public ReadOnlyObjectProperty<ClientStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

    public BooleanBinding connectedProperty() {
        return Bindings.createBooleanBinding(() -> status.get().equals(CONNECTED), status);
    }

}
