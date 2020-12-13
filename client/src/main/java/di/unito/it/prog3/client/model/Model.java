package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.model.EmailProperty;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.Emails;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.function.Predicate;

import static di.unito.it.prog3.libs.net.Request.Type.*;

public class Model {

    // Net client
    private final Client client;

    // E-mail queues
    private final ListProperty<Email> all;
    private final ObjectProperty<ObservableList<Email>> sent;
    private final ObjectProperty<ObservableList<Email>> received;

    // Currently selected/opened e-mail
    private final EmailProperty currentEmail;


    public Model(Application.Parameters parameters) {
        currentEmail = new EmailProperty();

        all = new SimpleListProperty<>(FXCollections.observableArrayList());
        received = new SimpleObjectProperty<>(createQueue(Queue.RECEIVED));
        sent = new SimpleObjectProperty<>(createQueue(Queue.SENT));

        client = new Client(this, parameters);
        client.startPoller();
    }

    public void send(Email email) {
        send(email, null);
    }

    public void send(Email email, Callback callback) {
        client.newRequest(SEND)
                .addAllRecipients(email.getRecipients())
                .setSubject(email.getSubject())
                .setBody(email.getBody())
                .onSuccess(response -> {
                    if (callback != null) callback.call();
                    all.addAll(response.getEmails());
                })
                .send();
    }

    public void setOpened(Email email) {
        if (email.isUnread()) {
            email.setRead(true);

            client.newRequest(OPEN)
                    .setId(email.getId())
                    .send();
        }
    }

    public Email openCurrentEmail() {
        Email email = currentEmail.get();
        setOpened(email);
        return email;
    }

    public void delete(Email.ID id) {
        delete(id, null);
    }

    public void delete(Email.ID id, Callback callback) {
        client.newRequest(DELETE)
                .setId(id)
                .onSuccess(response -> {
                    if (callback != null) callback.call();
                    all.removeIf(email -> email.getId().equals(id));
                })
                .send();
    }

    void loadNewerReceived() {
        Email lastReceived = received.getValue().get(0);
        // Email.ID offset = lastReceived.getId();

        LocalDateTime pivot;

        if (lastReceived != null) {
            pivot = lastReceived.getTimestamp();
        } else {
            pivot = LocalDateTime.now();
        }

        client.newRequest(READ)
                .setPivot(pivot)
                .setQueue(Queue.RECEIVED)
                .onSuccess(response ->
                        all.addAll(response.getEmails()))
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

    ListProperty<Email> allQueue() {
        return all;
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
