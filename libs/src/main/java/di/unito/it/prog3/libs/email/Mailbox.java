package di.unito.it.prog3.libs.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import di.unito.it.prog3.libs.utils.Emails;

public class Mailbox {

    private final String user;
    private final String domain;

    private Mailbox(String user, String domain) {
        this.user = user;
        this.domain = domain;
    }

    @JsonCreator
    public static Mailbox fromString(String email) {
        if (Emails.isWellFormed(email)) {
            String[] parts = email.split("@");
            return new Mailbox(parts[0], parts[1]);
        } else return null;
    }

    public String getUser() {
        return user;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return user + "@" + domain;
    }

}
