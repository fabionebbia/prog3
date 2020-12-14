package di.unito.it.prog3.libs.net2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.net.Chrono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Consumer;

public class ReadRequest extends Request {
    private Chrono direction;
    private LocalDateTime pivot;
    private Queue queue;
    private int many;


    public Chrono getDirection() {
        return direction;
    }

    public void setDirection(Chrono direction) {
        this.direction = direction;
    }

    public LocalDateTime getPivot() {
        return pivot;
    }

    @JsonProperty
    public void setPivot(LocalDateTime pivot) {
        this.pivot = pivot;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public int getMany() {
        return many;
    }

    public void setMany(int many) {
        this.many = many;
    }


    public static final class ReadRequestBuilder extends RequestBuilder<ReadRequest> {
        ReadRequestBuilder(Consumer<RequestBuilder<ReadRequest>> commitConsumer) {
            super(ReadRequest::new, commitConsumer);
        }

        public ReadRequestBuilder setDirection(Chrono direction) {
            request.setDirection(direction);
            return this;
        }

        public LocalDateTime getPivot() {
            return request.getPivot();
        }

        public ReadRequestBuilder setPivot(LocalDateTime pivot) {
            request.setPivot(pivot);
            return this;
        }

        public ReadRequestBuilder setQueue(Queue queue) {
            request.setQueue(queue);
            return this;
        }

        public ReadRequestBuilder setMany(int many) {
            request.setMany(many);
            return this;
        }
    }

}
