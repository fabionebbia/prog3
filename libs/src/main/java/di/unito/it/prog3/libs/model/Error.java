package di.unito.it.prog3.libs.model;

import di.unito.it.prog3.libs.net.Response;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Error {

    private final String title;
    private final String header;
    private final String content;

    public Error(String title, String header, String content) {
        this.title = title;
        this.header = header;
        this.content = content;
    }

    public Error(String title, String content) {
        this(title, null, content);
    }

    public String getTitle() {
        return title;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public void display() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(null);
            alert.setTitle(title);
            if (header != null) {
                alert.setHeaderText(header);
            }
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public static void display(String title, String header, Response response) {
        new Error(title, header, response.getMessage()).display();
    }

}
