package di.unito.it.prog3.libs.pojos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import di.unito.it.prog3.libs.exceptions.MalformedEmailIDException;
import di.unito.it.prog3.libs.store.Queue;

public class ID {

    private final Mailbox mailbox;
    private final Queue queue;
    private final int id;

    public ID(Mailbox mailbox, Queue queue, int id) {
        this.mailbox = mailbox;
        this.queue = queue;
        this.id = id;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    public Queue getQueue() {
        return queue;
    }

    public int getRelativeID() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        ID o;
        return other != null && other.getClass() == getClass()
                && (o = (ID) other).mailbox.equals(mailbox)
                && o.queue == queue
                && o.id == id;
    }

    @Override
    @JsonValue
    public String toString() {
        return mailbox + "/" + queue.asPath() + "/" + String.format("%04d", id);
    }

    @JsonCreator
    public static ID fromString(String strID) {
        String[] parts = strID.split("/");
        Mailbox mailbox;
        if (parts.length == 3 && (mailbox = Mailbox.from(parts[0])) != null) {
            return new ID(mailbox, Queue.fromShortPath(parts[1]), Integer.parseInt(parts[2]));
        } else throw new MalformedEmailIDException(strID);
    }
}

