package di.unito.it.prog3.libs.net.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import di.unito.it.prog3.libs.email.Email;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public abstract class Request {

    private RequestType type;
    private String user;
    private Email.ID id;

    @JsonCreator
    Request() {}

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public RequestType getType() {
        return type;
    }

}
