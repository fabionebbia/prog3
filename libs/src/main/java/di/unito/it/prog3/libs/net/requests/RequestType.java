package di.unito.it.prog3.libs.net.requests;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RequestType {

    LOGIN;

    @JsonValue
    public String forJackson() {
        return name().toLowerCase().replace('_', '-');
    }

}
