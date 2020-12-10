package di.unito.it.prog3.libs.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Mailbox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmailProperty extends SimpleObjectProperty<Email> {

    private final ReadOnlyObjectWrapper<ID> id;
    private final ReadOnlyStringWrapper sender;
    private final ListProperty<String> recipients;
    private final ReadOnlyStringWrapper subject;
    private final ReadOnlyObjectWrapper<LocalDateTime> dateSent;
    private final ReadOnlyStringWrapper body;

    public EmailProperty() {
        id = new ReadOnlyObjectWrapper<>();
        sender = new ReadOnlyStringWrapper();
        recipients = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
        subject = new ReadOnlyStringWrapper();
        dateSent = new ReadOnlyObjectWrapper<>();
        body = new ReadOnlyStringWrapper();

        addListener(((observable, oldMail, newEmail) -> {
            if (newEmail == null) return;

            id.set(newEmail.getId());
            sender.set(newEmail.getSender().toString());

            List<String> tmp = newEmail.getRecipients().stream()
                    .map(Mailbox::toString)
                    .collect(Collectors.toList());
            recipients.setAll(tmp);

            subject.set(newEmail.getSubject());
            dateSent.set(newEmail.getTimestamp());
            body.set(newEmail.getBody());
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

}
