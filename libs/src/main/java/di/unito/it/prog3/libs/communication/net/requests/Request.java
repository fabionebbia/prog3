package di.unito.it.prog3.libs.communication.net.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public abstract class Request {

    private String user;

    @JsonCreator
    Request() {}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
