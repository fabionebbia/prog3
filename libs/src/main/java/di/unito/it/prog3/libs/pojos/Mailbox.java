package di.unito.it.prog3.libs.pojos;

import di.unito.it.prog3.libs.utils.Emails;

public class Mailbox {

    private final String user;
    private final String domain;

    private Mailbox(String user, String domain) {
        this.user = user;
        this.domain = domain;
    }

    public static Mailbox from(String email) {
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
