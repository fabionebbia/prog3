package di.unito.it.prog3.server.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import di.unito.it.prog3.libs.communication.net.requests.Request;

import java.io.IOException;
import java.net.Socket;

public class SocketHandler implements Runnable {

    private final ObjectMapper json;
    private final Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        json = Server.getObjectMapper();
    }

    @Override
    public void run() {
        try {
            Request request = json.readValue(socket.getInputStream(), Request.class);
            System.out.println(request.getClass().getSimpleName());
        } catch (IOException e) {
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
