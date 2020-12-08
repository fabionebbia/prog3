package di.unito.it.prog3.server.server;

import di.unito.it.prog3.libs.communication.net.JsonMapper;
import di.unito.it.prog3.libs.communication.net.requests.Request;
import di.unito.it.prog3.libs.communication.net.responses.Response;

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
        // logger.addHandler(new ConsoleHandler());
    }

    @Override
    public void run() {
        logger.info("SocketHandler started, handling request..");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            Request request = json.readValue(bufferedReader, Request.class);
            logger.info("Received " + request.getClass().getSimpleName());

            Response response = new Response();
            response.setSuccess(true);
            response.setMessage("Hey");
            json.writeValue(socket.getOutputStream(), response);

            logger.info("Response sent");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                logger.info("Socket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.info("SocketHandler closed");
    }

}
