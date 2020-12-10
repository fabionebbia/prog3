package di.unito.it.prog3.libs.net.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class StoreRequest extends Request {

    private List<String> recipients;
    private String subject;
    private String body;

    public StoreRequest(@JsonProperty("recipients") List<String> recipients,
                        @JsonProperty("subject") String subject,
                        @JsonProperty("body") String body) {
        this.recipients = new ArrayList<>();
        this.recipients.addAll(recipients);
        this.subject = subject;
        this.body = body;
    }

}
