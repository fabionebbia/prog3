package di.unito.it.prog3.server.gui;

import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.net.Request;

public class LogSession {

    private final StringBuilder stringBuilder;

    // indicates whether the next appended message is a new line
    private boolean lineBegin;


    /**
     * Initializes the log session based on the received request.
     *
     * @param request The request this log session refers to.
     */
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


    /**
     * Appends a new message.
     *
     * @param piece The new messaged that must be appended.
     * @return The updated log session.
     */
    public LogSession append(String piece) {
        if (lineBegin) {
            stringBuilder.append("\t");
            lineBegin = false;
        }
        stringBuilder.append(piece);
        return this;
    }


    /**
     * Appends a new message followed by a new line character.
     *
     * @param piece The new messaged that must be appended.
     * @return The updated log session.
     */
    public LogSession appendln(String piece) {
        append(piece).append("\n");
        lineBegin = true;
        return this;
    }


    /**
     * Logs the final request response.
     *
     * @param response The response.
     * @return The updated log session.
     */
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
