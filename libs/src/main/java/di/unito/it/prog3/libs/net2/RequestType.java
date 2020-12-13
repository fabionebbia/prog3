package di.unito.it.prog3.libs.net2;

import java.util.function.Consumer;
import java.util.function.Supplier;
import di.unito.it.prog3.libs.net2.LoginRequest.LoginRequestBuilder;
import di.unito.it.prog3.libs.net2.SendRequest.SendRequestBuilder;
import di.unito.it.prog3.libs.net2.OpenRequest.OpenRequestBuilder;
import di.unito.it.prog3.libs.net2.ReadRequest.ReadRequestBuilder;
import di.unito.it.prog3.libs.net2.DeletionRequest.DeletionRequestBuilder;

public interface RequestType<R extends Request, B extends RequestBuilder<R>>
        extends RequestBuilderSupplier<R, B> {

    RequestBuilderSupplier<LoginRequest, LoginRequest.LoginRequestBuilder> LOGIN = LoginRequestBuilder::new;


    RequestBuilderSupplier<ReadRequest, ReadRequestBuilder> READ = ReadRequestBuilder::new;

    RequestBuilderSupplier<SendRequest, SendRequestBuilder> SEND = SendRequestBuilder::new;

    RequestBuilderSupplier<OpenRequest, OpenRequestBuilder> OPEN = OpenRequestBuilder::new;

    RequestBuilderSupplier<DeletionRequest, DeletionRequestBuilder> DELETE = DeletionRequestBuilder::new;

}
