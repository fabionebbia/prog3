package di.unito.it.prog3.libs.communication.net.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import di.unito.it.prog3.libs.email.Email;

import java.util.List;

public class ReadResponse extends Response {

    private final List<Email> emails;

    public ReadResponse(@JsonProperty("emails") List<Email> emails) {
        this.emails = emails;
    }

    public List<Email> getEmails() {
        return emails;
    }

}
