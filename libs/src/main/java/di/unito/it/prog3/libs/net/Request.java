package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@JsonInclude(Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Request {

    static final class LoginRequest extends Request {
        public LoginRequest(String user) {
            super(Type.LOGIN, user);
        }
    }

    static final class SendRequest extends Request {
        public SendRequest(String user) {
            super(Type.LOGIN, user);
        }
        public void addAllRecipients(Collection<String> recipients) {
            super.addAllRecipients(recipients);
        }
        public void setSubject(String subject) {
            super.setSubject(subject);
        }
        public void
    }

    private Type type;
    private String user;
    private Email.ID id;
    private Chrono direction;
    private int many;
    private Queue queue;
    private Instant pivot;

    private Set<String> recipients;
    private String subject;
    private String body;

    @JsonIgnore
    private ResponseHandler successHandler;

    @JsonIgnore
    private ResponseHandler failureHandler;

    private static final ResponseHandler defaultFailureHandler = response -> {
        throw new RuntimeException(response.getMessage());
    };


    @JsonCreator
    private Request() {
        recipients = new HashSet<>();
    }

    public Request(Type type, String user) {
        this();
        this.type = type;
        this.user = user;
    }

    public Instant getPivot() {
        return pivot;
    }

    public String getUser() {
        return user;
    }

    public Email.ID getId() {
        return id;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public void setId(Email.ID id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public Chrono getDirection() {
        return direction;
    }

    public void setDirection(Chrono direction) {
        this.direction = direction;
    }

    public int getMany() {
        return many;
    }

    public void setMany(int many) {
        this.many = many;
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
    }

    protected void addRecipient(String recipient) {
        recipients.add(recipient);
    }

    protected void addAllRecipients(Collection<String> recipients) {
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















    public void gotResponse(Response response) {
        if (response.successful()) {
            if (successHandler != null) {
                successHandler.handle(response);
            }
        } else {
            (failureHandler != null ? failureHandler : defaultFailureHandler).handle(response);
        }
    }


    public static class RequestBuilder {
        private final Request request;

        public RequestBuilder(Type type, String user) {
            request = new Request(type, user);
        }

        private Consumer<Request> sendConsumer;
        public RequestBuilder(Type type, String user, Consumer<Request> sendConsumer) {
            request = new Request(type, user);
            this.sendConsumer = sendConsumer;
        }

        public RequestBuilder setId(Email.ID id) {
            request.id = id;
            return this;
        }

        public RequestBuilder setQueue(Queue queue) {
            request.queue = queue;
            return this;
        }

        public RequestBuilder addRecipient(String recipient) {
            request.recipients.add(recipient);
            return this;
        }

        public RequestBuilder setDirection(Chrono direction) {
            request.direction = direction;
            return this;
        }

        public RequestBuilder setOffset(Email.ID offset) {
            return setId(offset);
        }

        public RequestBuilder setMany(int many) {
            request.many = many;
            return this;
        }

        public RequestBuilder setPivot(Instant pivot) {
            request.pivot = pivot;
            return this;
        }

        public RequestBuilder setPivot(LocalDateTime pivot) {
            setPivot(pivot.atZone(ZoneId.systemDefault()).toInstant());
            return this;
        }

        public RequestBuilder addAllRecipients(Collection<String> recipients) {
            request.recipients.addAll(recipients);
            return this;
        }

        public RequestBuilder setSubject(String subject) {
            request.subject = subject;
            return this;
        }

        public RequestBuilder setBody(String body) {
            request.body = body;
            return this;
        }

        public RequestBuilder onSuccess(ResponseHandler successHandler) {
            request.successHandler = successHandler;
            return this;
        }

        public RequestBuilder onFailure(ResponseHandler failureHandler) {
            request.failureHandler = failureHandler;
            return this;
        }

        public void send() {
            sendConsumer.accept(request);
        }
    }


    public enum Type {
        LOGIN,
        SEND,
        READ,
        OPEN,
        DELETE;

        @JsonValue
        public String forJackson() {
            return name().toLowerCase().replace('_', '-');
        }

    }

}
