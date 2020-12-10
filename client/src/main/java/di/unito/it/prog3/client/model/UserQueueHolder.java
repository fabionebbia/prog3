package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Mailbox;
import di.unito.it.prog3.libs.email.Queue;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.Comparator;
import java.util.function.Predicate;

class UserQueueHolder {

    private ListProperty<Email> all;
    private ObjectProperty<ObservableList<Email>> received;
    private ObjectProperty<ObservableList<Email>> sent;
    private Mailbox user;

    UserQueueHolder() {
        all = new SimpleListProperty<>();
        received = new SimpleObjectProperty<>(createQueue(Queue.RECEIVED));
        sent = new SimpleObjectProperty<>(createQueue(Queue.SENT));
    }

    void init(String user, ListProperty<Email> all) {
        this.user = Mailbox.fromString(user);
        this.all.bind(all);

        /*received.set();
        sent.set();*/
    }

    ObservableValue<ObservableList<Email>> getQueue(Queue queue) {
        return switch (queue) {
            case RECEIVED -> received;
            case SENT -> sent;
            case DRAFTS -> null;
        };
    }

    private ObservableList<Email> createQueue(Queue queue) {
        Predicate<Email> filter = getFilterFor(queue);
        FilteredList<Email> filtered = new FilteredList<>(all, filter);
        return new SortedList<>(filtered, Comparator.comparing(Email::getTimestamp));
    }

    private Predicate<Email> getFilterFor(Queue queue) {
        /*return switch (queue) {
            case RECEIVED ->
            case SENT ->
                    email -> email.getQueue().equals(queue);
            case DRAFTS -> Email::isDraft;
        };*/return email -> email.getQueue().equals(queue);
    }

}
