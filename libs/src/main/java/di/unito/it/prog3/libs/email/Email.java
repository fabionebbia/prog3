package di.unito.it.prog3.libs.email;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import di.unito.it.prog3.libs.exceptions.MalformedEmailIDException;
import di.unito.it.prog3.libs.store.Queue;

import java.time.LocalDateTime;
import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Email {

    public static final Email EMPTY = new Email();

    @JsonIgnore
    private Mailbox mailbox;

    @JsonIgnore
    private Queue queue;

    @JsonIgnore
    private UUID relativeId;

    private Mailbox sender;
    private String subject;
    private final List<Mailbox> recipients;
    private LocalDateTime timestamp;
    private String body;
    private boolean read;

    @JsonCreator // for Jackson deserialization
    public Email() {
        recipients = new ArrayList<>();
    }

    public Email(Email e) {
        this.mailbox = e.getMailbox();
        this.queue = e.getQueue();
        this.relativeId = e.getRelativeId();

        this.read = e.isRead();
        this.body = e.getBody();
        this.sender = e.getSender();
        this.subject = e.getSubject();
        this.timestamp = e.getTimestamp();
        this.recipients = e.getRecipients();
    }

    public ID getId() {
        return new ID(mailbox, queue, relativeId);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return read;
    }

    @JsonIgnore
    public boolean isUnread() {
        return !isRead();
    }

    public Mailbox getSender() {
        return sender;
    }

    public void setSender(Mailbox sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void timestamp() {
        timestamp = LocalDateTime.now();
    }

    public List<Mailbox> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public void addRecipient(Mailbox recipient) {
        recipients.add(recipient);
    }

    public void addRecipients(Mailbox... recipients) {
        for (Mailbox recipient : recipients) {
            addRecipient(recipient);
        }
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    public void setMailbox(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public UUID getRelativeId() {
        return relativeId;
    }

    @Override
    public String toString() {
        return getId().toString();
    }

    public static Email writeNew() {
        return new Email();
    }

    public class ID {

        private final Mailbox mailbox;
        private final Queue queue;
        private final UUID relativeId;

        private ID(Mailbox mailbox, Queue queue, UUID relativeId) {
            this.relativeId = relativeId;
            this.mailbox = mailbox;
            this.queue = queue;
        }

        public Mailbox getMailbox() {
            return mailbox;
        }

        public Queue getQueue() {
            return queue;
        }

        public UUID getRelativeId() {
            return relativeId;
        }

        @JsonCreator
        private ID fromString(String str) {
            String[] parts = str.split("/");

            if (parts.length == 3) {
                Mailbox mailbox = Mailbox.fromString(parts[0]);
                Queue queue = Queue.fromShortPath(parts[1]);
                UUID uuid = UUID.fromString(parts[2]);

                return new ID(mailbox, queue, uuid);
            } else throw new MalformedEmailIDException(str);
        }

        @Override
        public boolean equals(Object other) {
            ID o;
            return other != null && other.getClass() == getClass()
                    && (o = (ID) other).mailbox.equals(mailbox)
                    && o.relativeId.equals(relativeId)
                    && o.queue.equals(queue);
        }

        @Override
        @JsonValue
        public String toString() {
            return mailbox + "/" + queue.asShortPath() + "/" + String.format("%04d", relativeId);
        }
    }

}
