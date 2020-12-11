package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import di.unito.it.prog3.libs.email.Email;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Response {

    private boolean success;
    private String message;
    private Set<Email> emails;

    @JsonCreator
    private Response() {
        emails = new HashSet<>();
    }

    private Response(boolean success, String message, Set<Email> emails) {
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

    public Set<Email> getEmails() {
        return emails;
    }


    private static final Response SUCCESS = new Response(true, "", null);

    public static Response success() {
        return SUCCESS;
    }

    public static Response success(Email... emails) {
        return success(Set.of(emails));
    }

    public static Response success(Set<Email> emails) {
        return new Response(true, "", emails);
    }

    public static Response failure(String message) {
        return new Response(false, message, null);
    }

    public static Response failure(Throwable t) {
        return failure(t.getMessage());
    }

}
