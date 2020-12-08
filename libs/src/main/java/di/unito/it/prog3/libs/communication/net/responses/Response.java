package di.unito.it.prog3.libs.communication.net.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import di.unito.it.prog3.libs.email.Email;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Response {

    private boolean success;
    private String message;
    private List<Email> emails;


    public boolean success() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

}
