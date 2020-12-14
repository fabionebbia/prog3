package di.unito.it.prog3.libs.net2;

import di.unito.it.prog3.libs.email.Email;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
        ensure(id != null, "Missing e-mail id");
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
