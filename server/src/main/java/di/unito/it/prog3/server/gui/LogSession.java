package di.unito.it.prog3.server.gui;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;

public class LogSession {

    private final StringBuilder stringBuilder;
    private boolean lineBegin;

    public LogSession(Request request) {
        stringBuilder = new StringBuilder();

        String user = request.getUser();
        String requestType = request.getClass().getSimpleName();
        stringBuilder.append("Received ")
                .append(requestType)
                .append(" from ")
                .append(user)
                .append("\n");
        lineBegin = true;
    }

    public LogSession append(String piece) {
        if (lineBegin) {
            stringBuilder.append("\t");
            lineBegin = false;
        }
        stringBuilder.append(piece);
        return this;
    }

    public LogSession appendln(String piece) {
        append(piece).append("\n");
        lineBegin = true;
        return this;
    }

    public LogSession appendln(Response response) {
        stringBuilder.append(response.successful() ? " [Success]" : " [Failure]");
        String message = response.getMessage();
        if (message != null && !message.isBlank()) {
            append(": " + message);
        }
        appendln("");
        return this;
    }

    public String commit() {
        return stringBuilder.toString();
    }

}
