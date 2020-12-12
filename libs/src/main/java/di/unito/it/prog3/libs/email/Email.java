package di.unito.it.prog3.libs.email;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import di.unito.it.prog3.libs.exceptions.MalformedEmailIDException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Email {

    public static final Email EMPTY = new Email();


    @JsonIgnore
    private String mailbox;

    @JsonIgnore
    private Queue queue;

    @JsonIgnore
    private UUID relativeId;

    private String sender;
    private String subject;
    private final Set<String> recipients;
    private LocalDateTime timestamp;
    private String body;
    private boolean read;
    private boolean draft;

    public Email() {
        recipients = new HashSet<>();
    }

    public Email(Email other) {
        this();
        this.mailbox = other.mailbox;
        this.queue = other.queue;
        this.relativeId = other.relativeId;
        this.sender = other.sender;
        this.subject = other.subject;
        this.recipients.addAll(other.recipients);
        this.timestamp = other.timestamp;
        this.body = other.body;
        this.read = other.read;
        this.draft = other.draft;
    }

    public ID getId() {
        return new ID(mailbox, queue, relativeId);
    }

    public void setId(ID id) {
        mailbox = id.mailbox;
        queue = id.queue;
        relativeId = id.relativeId;
    }

    public void setRelativeId(UUID relativeId) {
        this.relativeId = relativeId;
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

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isDraft() {
        return draft;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
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

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Set<String> getRecipients() {
        return Collections.unmodifiableSet(recipients);
    }

    public void addRecipient(String recipient) {
        recipients.add(recipient);
    }

    public void addAllRecipients(Collection<String> recipients) {
        this.recipients.addAll(recipients);
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
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


    public static class ID {

        private final String mailbox;
        private final Queue queue;
        private final UUID relativeId;

        private ID(String mailbox, Queue queue, UUID relativeId) {
            this.relativeId = relativeId;
            this.mailbox = mailbox;
            this.queue = queue;
        }

        public String getMailbox() {
            return mailbox;
        }

        public Queue getQueue() {
            return queue;
        }

        public UUID getRelativeId() {
            return relativeId;
        }

        @JsonCreator
        public static ID fromString(String str) {
            String[] parts = str.split("/");

            if (parts.length == 3) {
                String mailbox = parts[0];
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
            return (mailbox != null ? mailbox : "NO_MAILBOX")
                    + "/" + (queue != null ? queue.asShortPath() : "NO_QUEUE")
                    + "/" + (relativeId != null ? relativeId : "NO_RELATIVE_ID");
        }
    }

}
