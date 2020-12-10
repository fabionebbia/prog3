package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import di.unito.it.prog3.libs.email.Email;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Response {

    private boolean success;
    private String message;
    private List<Email> emails;

    @JsonCreator
    private Response() {}

    private Response(boolean success, String message, List<Email> emails) {
        this.success = success;
        this.message = message;
        this.emails = emails;
    }

    public boolean successful() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public static final Response SUCCESS = new Response(true, "", null);

    public static Response success(List<Email> emails) {
        return new Response(true, "", emails);
    }

    public static Response failure(String message) {
        return new Response(false, message, null);
    }

    public static Response failure(Throwable t) {
        return failure(t.getMessage());
    }

}
