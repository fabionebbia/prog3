package di.unito.it.prog3.libs.pojos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import di.unito.it.prog3.libs.utils.Emails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Email {

    public static final Email EMPTY = new Email();

    private ID id;
    private String sender;
    private List<String> recipients;
    private String subject;
    private Date dateSent;
    private String body;

    @JsonCreator // for Jackson deserialization
    private Email() {
        recipients = new ArrayList<>();
    }

    private Email(ID id) {
        this();
        this.id = id;
    }

    public Email(Email e) {
        this.id = e.id;
        this.body = e.body;
        this.sender = e.sender;
        this.subject = e.subject;
        this.dateSent = e.dateSent;
        recipients = new ArrayList<>();
        recipients.addAll(e.recipients);
    }

    public String toJson() {
        try {
            return new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ID getID() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    public String getSubject() {
        return subject;
    }

    public Date getDateSent() {
        return new Date(dateSent.getTime());
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String[] lines = body.split("[\n\r]");
        int maxLineLen = lines[0].length();
        for (String line : lines) {
            if (line.length() > maxLineLen) {
                maxLineLen = line.length();
            }
        }
        int headerLen = id.toString().length() + 20;
        int sepLen = Math.max(headerLen, maxLineLen);

        sb.append("\n+-------( ")
                .append(id)
                .append(" )-------+\n  Sender: ")
                .append(sender)
                .append("\n")
                .append("  Recipients: ");

        for (int i = 0; i < recipients.size(); i++) {
            sb.append(recipients.get(i));
            if (i != recipients.size() - 1) {
                sb.append(", ");
            }
        }

        String separator = "\n+" + String.format("%" + (sepLen - 1) + "s", "+").replace(' ', '-') + "\n";
        sb.append("\n  Sent: ")
                .append(dateSent)
                .append("\n  Subject: ")
                .append(subject != null ? subject : "No Subject")
                .append(separator);
        //.append(body != null ? body : "Empty Body")
        String[] bodyLines = (body != null ? body : "Empty Body").split("[\n\r]");
        for (int i = 0; i < bodyLines.length; i++) {
            sb.append("  ").append(bodyLines[i]);
            if (i != bodyLines.length - 1) {
                sb.append("\n");
            }
        }
        sb.append(separator);

        return sb.toString();
    }

    public static class EmailBuilder {
        private Email email;

        public EmailBuilder(ID id) {
            email = new Email(id);
        }

        public EmailBuilder(String id) {
            this(ID.fromString(id));
        }

        public EmailBuilder start(ID id) {
            email = new Email(id);
            return this;
        }

        public EmailBuilder start(String id) {
            return start(ID.fromString(id));
        }

        public EmailBuilder setSender(String sender) {
            if (Emails.isWellFormed(sender)) {
                email.sender = sender;
                return this;
            } else throw new EmailBuilderException("Malformed sender e-mail address.");
        }

        public EmailBuilder addRecipient(String recipient) {
            if (Emails.isWellFormed(recipient)) {
                email.recipients.add(recipient);
                return this;
            } else throw new EmailBuilderException("Malformed recipient e-mail address.");
        }

        public EmailBuilder setSubject(String subject) {
            email.subject = subject;
            return this;
        }

        public EmailBuilder setBody(String body) {
            email.body = body;
            return this;
        }

        public EmailBuilder setSentDate(Date sentDate) {
            if (sentDate.compareTo(new Date()) <= 0) {
                email.dateSent = sentDate;
                return this;
            } else throw new EmailBuilderException("Time travel not supported.");
        }

        public Email build() {
            if (email.sender == null) {
                throw new EmailBuilderException("Sender e-mail address unset.");
            }
            if (email.recipients.size() == 0) {
                throw new EmailBuilderException("E-mail has no recipients.");
            }
            if (email.dateSent == null) {
                throw new EmailBuilderException("Sent datetime unset.");
            }
            Email built = new Email(email);
            email = null;
            return built;
        }

        public static class EmailBuilderException extends IllegalArgumentException {
            private EmailBuilderException(String message) {
                super(message);
            }
        }
    }

}
