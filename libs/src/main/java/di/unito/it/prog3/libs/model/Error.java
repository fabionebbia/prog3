package di.unito.it.prog3.libs.model;

public class Error {

    private final String title;
    private final String header;
    private final String content;

    public Error(String title, String header, String content) {
        this.title = title;
        this.header = header;
        this.content = content;
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

}
