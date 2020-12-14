package di.unito.it.prog3.libs.net2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class SendRequest extends Request {

    private final Set<String> recipients;
    private String subject;
    private String body;

    public SendRequest() {
        recipients = new HashSet<>();
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public void addRecipient(String recipient) {
        recipients.add(recipient);
    }

    public void addAllRecipients(Collection<String> recipients) {
        this.recipients.addAll(recipients);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void validate() {
        super.validate();
        ensure(recipients.size() > 0, "Missing recipients");
    }

    public static final class SendRequestBuilder extends RequestBuilder<SendRequest> {
        SendRequestBuilder(Consumer<RequestBuilder<SendRequest>> commitConsumer) {
            super(SendRequest::new, commitConsumer);
        }

        public SendRequestBuilder addRecipient(String recipient) {
            request.addRecipient(recipient);
            return this;
        }

        public SendRequestBuilder addAllRecipients(Collection<String> recipients) {
            request.addAllRecipients(recipients);
            return this;
        }

        public SendRequestBuilder setSubject(String subject) {
            request.setSubject(subject);
            return this;
        }

        public SendRequestBuilder setBody(String body) {
            request.setBody(body);
            return this;
        }
    }
}
