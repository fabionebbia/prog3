package di.unito.it.prog3.libs.communication.net.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import di.unito.it.prog3.libs.email.Email.ID;

abstract class EmailRequest extends Request {

    protected final ID id;

    EmailRequest(@JsonProperty("id") ID id) {
        super();
        this.id = id;
    }

}
