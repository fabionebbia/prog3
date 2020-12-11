package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.model.EmailProperty;
import di.unito.it.prog3.libs.net.Chrono;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Predicate;

import static di.unito.it.prog3.libs.net.Request.Type.READ;
import static di.unito.it.prog3.libs.net.Request.Type.SEND;

public class Model {

    // E-mail queues
    private final ListProperty<Email> all;
    private final ObjectProperty<ObservableList<Email>> sent;
    private final ObjectProperty<ObservableList<Email>> received;

    private final EmailProperty currentEmail;
    private final Client client;

    public Model(Application.Parameters parameters) {
        client = new Client(this, parameters);

        currentEmail = new EmailProperty();
        all = new SimpleListProperty<>(FXCollections.observableArrayList());
        received = new SimpleObjectProperty<>(createQueue(Queue.RECEIVED));
        sent = new SimpleObjectProperty<>(createQueue(Queue.SENT));

        Email.ID sentEmailId = Email.ID.fromString("me@email.tld/R/" + UUID.randomUUID().toString());
        Email.ID receivedReplyId = Email.ID.fromString("me@email.tld/S/" + UUID.randomUUID().toString());

        Email sentEmail = new Email();
        sentEmail.setId(sentEmailId);
        sentEmail.setSender("me@email.tld");
        sentEmail.setSubject("This is my original message");
        sentEmail.setBody("With its interesting body");
        sentEmail.addRecipient("someone@email.tld");
        sentEmail.setTimestamp(LocalDateTime.now());


        Email receivedReply = new Email();
        receivedReply.setId(receivedReplyId);
        receivedReply.setSender("someone@email.tld");
        receivedReply.setSubject("This is someone else's reply");
        receivedReply.setBody("With its interesting body too");
        receivedReply.addRecipient("me@email.tld");
        receivedReply.addRecipient("whoknows@email.tld");
        receivedReply.setTimestamp(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));

        all.addAll(sentEmail, receivedReply);

        client.newRequest(READ)
                .setQueue(Queue.RECEIVED)
                .setMany(10)
                .onSuccess(response -> all.addAll(response.getEmails()))
                .send();
    }

    public void send(Email email) {
        client.newRequest(SEND)
                .addAllRecipients(email.getRecipients())
                .setSubject(email.getSubject())
                .setBody(email.getBody())
                .onSuccess(response -> {
                    System.out.println(response.getEmails());
                    all.addAll(response.getEmails());
                })
                .send();
    }

    public Client getClient() {
        return client;
    }

    public Email getCurrentEmail() {
        return currentEmail.get();
    }

    public void setCurrentEmail(Email email) {
        currentEmail.set(email);
    }

    public void clearCurrentEmail() {
        setCurrentEmail(null);
    }

    public BooleanBinding isCurrentEmailSet() {
        return currentEmail.isNotNull();
    }

    public EmailProperty currentEmailProperty() {
        return currentEmail;
    }

    public ObservableValue<ObservableList<Email>> receivedQueue() {
        return received;
    }

    public ObservableValue<ObservableList<Email>> sentQueue() {
        return sent;
    }

    // TODO public ???
    public void shutdown() throws InterruptedException {
        client.shutdown();
    }

    private ObservableList<Email> createQueue(Queue queue) {
        Predicate<Email> filter = email -> email.getQueue().equals(queue);
        FilteredList<Email> filtered = new FilteredList<>(all, filter);
        return new SortedList<>(filtered, Comparator.comparing(Email::getTimestamp).reversed());
    }

}
