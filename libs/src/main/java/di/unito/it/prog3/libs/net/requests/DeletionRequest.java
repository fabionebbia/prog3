package di.unito.it.prog3.libs.net.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import di.unito.it.prog3.libs.email.Email.ID;

public class DeletionRequest extends EmailRequest {

    public DeletionRequest(@JsonProperty("id") ID id) {
        super(id);
    }

}
