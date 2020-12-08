package di.unito.it.prog3.libs.communication.net.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import di.unito.it.prog3.libs.email.Email.ID;

public class ReadSingleRequest extends EmailRequest {

    public ReadSingleRequest(@JsonProperty("id") ID id) {
        super(id);
    }

    public ID getId() {
        return id;
    }
}