package di.unito.it.prog3.libs.net;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Emails;
import di.unito.it.prog3.libs.utils.ValueCallback;
import javafx.util.Callback;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Request {

    private String user;
    private Email.ID id;
    private Type type;

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

    public String getUser() {
        return user;
    }

    public Email.ID getId() {
        return id;
    }

    public void setId(Email.ID id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public Set<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<String> recipients) {
        this.recipients = recipients;
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
            Objects.requireNonNull(successHandler, "Success handler unset");
            successHandler.handle(response);
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

        public RequestBuilder addRecipient(String recipient) {
            request.recipients.add(recipient);
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
            if (request.successHandler == null) {
                throw new IllegalStateException("Need to se a success request handler");
            }
            sendConsumer.accept(request);
        }
    }


    public enum Type {
        LOGIN,
        SEND;

        @JsonValue
        public String forJackson() {
            return name().toLowerCase().replace('_', '-');
        }

    }

}
