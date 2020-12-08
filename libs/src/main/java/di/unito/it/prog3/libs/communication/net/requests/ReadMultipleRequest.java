package di.unito.it.prog3.libs.communication.net.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import di.unito.it.prog3.libs.email.Email.ID;

public class ReadMultipleRequest extends EmailRequest {

    private final int many;

    public ReadMultipleRequest(@JsonProperty("id") ID offset,
                               @JsonProperty("many") int many) {
        super(offset);
        this.many = many;
    }

    public ID getOffset() {
        return id;
    }

    public int getMany() {
        return many;
    }

}
