package di.unito.it.prog3.client.fxml.model;

import com.sun.javafx.collections.ObservableListWrapper;
import di.unito.it.prog3.libs.pojos.Email;
import di.unito.it.prog3.libs.pojos.ID;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;
import java.util.List;

public class EmailProperty {

    private final ReadOnlyObjectWrapper<Email> email;
    private final ReadOnlyObjectWrapper<ID> id;
    private final ReadOnlyStringWrapper sender;
    private final ReadOnlyListWrapper<String> recipients;
    private final ReadOnlyStringWrapper subject;
    private final ReadOnlyObjectWrapper<Date> dateSent;
    private final ReadOnlyStringWrapper body;

    private boolean modifiable;

    public EmailProperty() {
        this(Email.EMPTY);
    }

    public EmailProperty(Email email) {
        this.email = new ReadOnlyObjectWrapper<>(email);
        id = new ReadOnlyObjectWrapper<>(email.getID());
        sender = new ReadOnlyStringWrapper(email.getSender());

        ObservableList<String> tmp = FXCollections.observableArrayList(email.getRecipients());

        recipients = new ReadOnlyListWrapper<>(tmp);
        //recipients.get().addAll(email.getRecipients());
        subject = new ReadOnlyStringWrapper(email.getSubject());
        dateSent = new ReadOnlyObjectWrapper<>(email.getDateSent());
        body = new ReadOnlyStringWrapper(email.getBody());

        this.email.addListener(((observable, oldMail, newEmail) -> {
            id.set(newEmail.getID());
            sender.set(newEmail.getSender());
            recipients.setAll(newEmail.getRecipients());
            subject.set(email.getSubject());
            dateSent.set(email.getDateSent());
            body.set(newEmail.getBody());
        }));
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    public ListProperty<String> recipientsProperty() {
        return recipients;
    }

}
