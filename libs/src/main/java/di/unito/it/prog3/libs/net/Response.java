package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import di.unito.it.prog3.libs.email.Email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Response {

    private final List<Email> emails;
    private boolean success;
    private String message;

    @JsonCreator
    private Response() {
        emails = new ArrayList<>();
    }

    private Response(boolean success, String message, ArrayList<Email> emails) {
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


    public static Response success() {
        return new Response(true, "", null);
    }

    public static Response success(Email email) {
        ArrayList<Email> list = new ArrayList<>();
        list.add(email);
        return success(list);
    }

    public static Response success(Collection<Email> emails) {
        return new Response(true, "", new ArrayList<>(emails));
    }

    public static Response failure(String message) {
        return new Response(false, message, null);
    }

    public static Response failure(Throwable t) {
        return failure(t.getMessage());
    }

}
