package di.unito.it.prog3.client.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Queue;
import di.unito.it.prog3.libs.email.EmailProperty;
import di.unito.it.prog3.libs.net.DeletionRequest.DeletionRequestBuilder;
import di.unito.it.prog3.libs.net.OpenRequest.OpenRequestBuilder;
import di.unito.it.prog3.libs.net.SendRequest.SendRequestBuilder;
import di.unito.it.prog3.libs.utils.Callback;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.Comparator;
import java.util.function.Predicate;

import static di.unito.it.prog3.libs.net.RequestType.*;

public class Model {

    // Net client
    private final Client client;

    // E-mail queues
    private final ListProperty<Email> all;
    private final ObservableList<Email> sent;
    private final ObservableList<Email> received;

    // Currently selected/opened e-mail
    private final EmailProperty currentEmail;


    /**
     * Initializes the properties, the e-mail queues, the client and starts up the poller.
     *
     * @param parameters JavaFX application parameters.
     */
    public Model(Application.Parameters parameters) {
        currentEmail = new EmailProperty();

        all = new SimpleListProperty<>(FXCollections.observableArrayList());
        received = createQueue(Queue.RECEIVED);
        sent = createQueue(Queue.SENT);

        client = new Client(this, parameters);
        client.startPoller();
    }


    /**
     * Asks the client to send the e-mail.
     *
     * @param email The e-mail that must be sent.
     */
    public void send(Email email) {
        send(email, null);
    }


    /**
     * Asks the server to send the e-mail and execute the callback on success.
     *
     * @param email The e-mail that must be sent.
     * @param callback The callback that must be called on success.
     */
    public void send(Email email, Callback callback) {
        SendRequestBuilder request = client.newRequest(SEND);

        request.addAllRecipients(email.getRecipients());
        request.setSubject(email.getSubject());
        request.setBody(email.getBody());

        request.setSuccessHandler(response -> {
            if (callback != null) callback.call();
            all.addAll(response.getEmails());
        });

        request.commit();
    }


    /**
     * If not already opened, opens the param e-mail and sends open request to the client.
     *
     * @param email The e-mail that must be opened.
     */
    public void setOpened(Email email) {
        if (email.isUnread()) {
            email.setRead(true);

            OpenRequestBuilder request = client.newRequest(OPEN);
            request.setId(email.getId());
            request.commit();
        }
    }


    /**
     * Opens the currently selected e-mail.
     *
     * @return The currently selected e-mail.
     */
    public Email openCurrentEmail() {
        Email email = currentEmail.get();
        setOpened(email);
        return email;
    }


    /**
     * Asks the server to delete the e-mail with the specified id.
     *
     * @param id The id of the e-mail that must be deleted.
     */
    public void delete(Email.ID id) {
        delete(id, null);
    }


    /**
     * Asks the server to delete the e-mail with the specified id
     * and execute the callback on success.
     *
     * @param id The id of the e-mail that must be deleted.
     * @param callback The callback that must be called on success.
     */
    public void delete(Email.ID id, Callback callback) {
        DeletionRequestBuilder request = client.newRequest(DELETE);

        request.setId(id);
        request.setSuccessHandler(response -> {
            if (callback != null) callback.call();
            all.removeIf(email -> email.getId().equals(id));
        });

        request.commit();
    }


    /**
     * Gives access to the net client.
     *
     * @return The net client.
     */
    public Client getClient() {
        return client;
    }


    /**
     * Allows controllers to retrieve the currenly selected e-mail bean.
     *
     * @return The currently selected e-mail bean.
     */
    public Email getCurrentEmail() {
        return currentEmail.get();
    }


    /**
     * Sets the currently selected e-mail.
     *
     * @param email The e-mail that must be set as currenly selected.
     */
    public void setCurrentEmail(Email email) {
        currentEmail.set(email);
    }


    /**
     * Clears current e-mail selection.
     */
    public void clearCurrentEmail() {
        setCurrentEmail(null);
    }


    /**
     * Lets the controllers know whether some e-mail is currently selected or not.
     *
     * @return Boolean binding whose value is true if an e-mail is currently selected, false otherwise.
     */
    public BooleanBinding isCurrentEmailSet() {
        return currentEmail.isNotNull();
    }


    /**
     * Lets the controllers see the currently selected e-mail property.
     *
     * @return Email property containing the currently selected e-mail property.
     */
    public EmailProperty currentEmailProperty() {
        return currentEmail;
    }


    /**
     * Exposes the list of the e-mails in the received queue.
     *
     * @return The list of the e-mails in the received queue.
     */
    public ObservableList<Email> receivedQueue() {
        return received;
    }


    /**
     * Exposes the list of the e-mails in the sent queue.
     *
     * @return The list of the e-mails in the sent queue.
     */
    public ObservableList<Email> sentQueue() {
        return sent;
    }


    /**
     * Exposes the list of all the e-mails in any queue to the classes in the same package.
     * Used by the client to update the queues after polling.
     *
     * @return The list of all the e-mails in any queue.
     */
    ListProperty<Email> allQueue() {
        return all;
    }


    /**
     * Starts an orderly shutdown process of the whole application.
     */
    public void shutdown() throws InterruptedException {
        client.shutdown();
    }


    /**
     * Utility used to easily create filtered sorted e-mail list representing queues.
     *
     * @param queue The queue that must be created.
     * @return The created list representing the queue.
     */
    private ObservableList<Email> createQueue(Queue queue) {
        Predicate<Email> filter = email -> email.getQueue().equals(queue);
        FilteredList<Email> filtered = new FilteredList<>(all, filter);
        return new SortedList<>(filtered, Comparator.comparing(Email::getTimestamp).reversed());
    }

}
