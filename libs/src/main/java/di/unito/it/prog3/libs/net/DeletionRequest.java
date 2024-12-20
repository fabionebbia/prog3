package di.unito.it.prog3.libs.net;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.Emails;

import java.util.function.Consumer;

public class DeletionRequest extends Request {

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
        ensure(Emails.isIdWellFormed(id), "Malformed e-mail id");
    }


    public static final class DeletionRequestBuilder extends RequestBuilder<DeletionRequest> {
        DeletionRequestBuilder(Consumer<RequestBuilder<DeletionRequest>> commitConsumer) {
            super(DeletionRequest::new, commitConsumer);
        }

        public DeletionRequestBuilder setId(Email.ID id) {
            request.setId(id);
            return this;
        }
    }

}
