package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.net.JsonMapper;
import di.unito.it.prog3.libs.net.requests.Request;
import di.unito.it.prog3.libs.net.responses.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketHandler implements Runnable {

    private final Logger logger;

    private final JsonMapper json;
    private final Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
        json = Server.getJsonMapper();
        logger = Logger.getLogger(getClass().getSimpleName());
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            Request request = json.readValue(bufferedReader, Request.class);
            logger.info("Received " + json.writeValueAsString(request));

            Response response = new Response();
            response.setSuccess(true);
            response.setMessage("Hey");
            json.writeValue(socket.getOutputStream(), response);
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
