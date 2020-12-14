package di.unito.it.prog3.libs.net;

import java.util.function.Consumer;

public final class LoginRequest extends Request {

    public static final class LoginRequestBuilder extends RequestBuilder<LoginRequest> {
        LoginRequestBuilder(Consumer<RequestBuilder<LoginRequest>> commitConsumer) {
            super(LoginRequest::new, commitConsumer);
        }
    }
}
