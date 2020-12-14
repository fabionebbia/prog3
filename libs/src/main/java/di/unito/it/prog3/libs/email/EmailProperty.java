package di.unito.it.prog3.libs.email;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EmailProperty extends SimpleObjectProperty<Email> {

    private final ReadOnlyObjectWrapper<LocalDateTime> timestamp;
    private final ListProperty<String> recipients;
    private final ReadOnlyStringWrapper subject;
    private final ReadOnlyObjectWrapper<ID> id;
    private final ReadOnlyStringWrapper sender;
    private final ReadOnlyStringWrapper body;

    public EmailProperty() {
        recipients = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        timestamp = new ReadOnlyObjectWrapper<>();
        subject = new ReadOnlyStringWrapper();
        sender = new ReadOnlyStringWrapper();
        id = new ReadOnlyObjectWrapper<>();
        body = new ReadOnlyStringWrapper();


        addListener(((observable, oldMail, newEmail) -> {
            if (newEmail != null) {
                recipients.setAll(newEmail.getRecipients());
                timestamp.set(newEmail.getTimestamp());
                subject.set(newEmail.getSubject());
                sender.set(newEmail.getSender());
                body.set(newEmail.getBody());
                id.set(newEmail.getId());
            }
        }));
    }

    public ListProperty<String> recipientsProperty() {
        return recipients;
    }

    public StringProperty fromProperty() {
        return sender;
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public StringProperty bodyProperty() {
        return body;
    }

    public ObjectPropertyBase<LocalDateTime> timestampProperty() {
        return timestamp;
    }

}
