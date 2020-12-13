package di.unito.it.prog3.libs.net2;

import java.util.function.Consumer;

public interface RequestBuilderSupplier<R extends Request, B extends RequestBuilder<R>> {

    B supply(Consumer<RequestBuilder<R>> commitConsumer);

}