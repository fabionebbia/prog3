package di.unito.it.prog3.libs.net2;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import di.unito.it.prog3.libs.utils.Emails;

@JsonTypeInfo(use = Id.NAME, property = "type")
public abstract class Request {

    protected String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void validate() {
        ensure(Emails.isWellFormed(user), "Missing user");
    }

    void ensure(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }

}
