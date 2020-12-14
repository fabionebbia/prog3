package di.unito.it.prog3.libs.net;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Emails;

import java.util.function.Consumer;

public class OpenRequest extends Request {

    private Email.ID id;


    public Email.ID getId() {
        return id;
    }

    @Override
    public void validate() {
        super.validate();
        ensure(Emails.isIdWellFormed(id), "Malformed e-mail id");
    }


    public static final class OpenRequestBuilder extends RequestBuilder<OpenRequest> {
        OpenRequestBuilder(Consumer<RequestBuilder<OpenRequest>> commitConsumer) {
            super(OpenRequest::new, commitConsumer);
        }

        public OpenRequestBuilder setId(Email.ID id) {
            request.id = id;
            return this;
        }
    }
}
