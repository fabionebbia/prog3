package di.unito.it.prog3.libs.communication.net.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest extends Request {

    public LoginRequest(@JsonProperty("user") String user) {
        setUser(user);
    }

}