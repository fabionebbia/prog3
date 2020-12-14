package di.unito.it.prog3.libs.net;

import di.unito.it.prog3.libs.net.LoginRequest.LoginRequestBuilder;
import di.unito.it.prog3.libs.net.SendRequest.SendRequestBuilder;
import di.unito.it.prog3.libs.net.OpenRequest.OpenRequestBuilder;
import di.unito.it.prog3.libs.net.ReadRequest.ReadRequestBuilder;
import di.unito.it.prog3.libs.net.DeletionRequest.DeletionRequestBuilder;

public interface RequestType<R extends Request, B extends RequestBuilder<R>>
        extends RequestBuilderSupplier<R, B> {

    RequestBuilderSupplier<LoginRequest, LoginRequest.LoginRequestBuilder> LOGIN = LoginRequestBuilder::new;

    RequestBuilderSupplier<ReadRequest, ReadRequestBuilder> READ = ReadRequestBuilder::new;

    RequestBuilderSupplier<SendRequest, SendRequestBuilder> SEND = SendRequestBuilder::new;

    RequestBuilderSupplier<OpenRequest, OpenRequestBuilder> OPEN = OpenRequestBuilder::new;

    RequestBuilderSupplier<DeletionRequest, DeletionRequestBuilder> DELETE = DeletionRequestBuilder::new;

}
