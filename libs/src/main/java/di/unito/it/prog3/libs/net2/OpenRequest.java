package di.unito.it.prog3.libs.net2;

import di.unito.it.prog3.libs.email.Email;

import java.util.function.Consumer;

public class OpenRequest extends Request {

    private Email.ID id;


    public Email.ID getId() {
        return id;
    }

    public void setId(Email.ID id) {
        this.id = id;
    }

    @Override
    public void validate() {
        super.validate();
        ensure(Email.ID.isWellformed(id), "Malformed e-mail id");
    }

    public static final class OpenRequestBuilder extends RequestBuilder<OpenRequest> {
        OpenRequestBuilder(Consumer<RequestBuilder<OpenRequest>> commitConsumer) {
            super(OpenRequest::new, commitConsumer);
        }

        public OpenRequestBuilder setId(Email.ID id) {
            request.setId(id);
            return this;
        }
    }
}
