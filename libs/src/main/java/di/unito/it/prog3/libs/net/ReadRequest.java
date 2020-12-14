package di.unito.it.prog3.libs.net;

import di.unito.it.prog3.libs.email.Queue;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class ReadRequest extends Request {

    private Chrono direction;
    private LocalDateTime pivot;
    private Queue queue;
    private int many;


    public Chrono getDirection() {
        return direction;
    }

    public LocalDateTime getPivot() {
        return pivot;
    }

    public Queue getQueue() {
        return queue;
    }

    public int getMany() {
        return many;
    }

    @Override
    public void validate() {
        super.validate();

        ensure(pivot != null, "Missing pivot");
        ensure(LocalDateTime.now().isAfter(pivot), "Time travel not allowed");

        ensure(direction != null, "Must specify a chronological reading direction");
    }


    public static final class ReadRequestBuilder extends RequestBuilder<ReadRequest> {
        ReadRequestBuilder(Consumer<RequestBuilder<ReadRequest>> commitConsumer) {
            super(ReadRequest::new, commitConsumer);
        }

        public ReadRequestBuilder setDirection(Chrono direction) {
            request.direction = direction;
            return this;
        }

        public ReadRequestBuilder setPivot(LocalDateTime pivot) {
            request.pivot = pivot;
            return this;
        }

        public ReadRequestBuilder setQueue(Queue queue) {
            request.queue = queue;
            return this;
        }

        public ReadRequestBuilder setMany(int many) {
            request.many = many;
            return this;
        }
    }

}
