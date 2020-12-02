package di.unito.it.prog3.client.fxml.model;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.email.Email.ID;
import di.unito.it.prog3.libs.email.Mailbox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EmailProperty {

    private final ReadOnlyObjectWrapper<Email> email;
    private final ReadOnlyObjectWrapper<ID> id;
    private final ReadOnlyStringWrapper sender;
    private final ReadOnlyListWrapper<String> recipients;
    private final ReadOnlyStringWrapper subject;
    private final ReadOnlyObjectWrapper<LocalDateTime> dateSent;
    private final ReadOnlyStringWrapper body;

    private boolean modifiable;

    public EmailProperty() {
        email = new ReadOnlyObjectWrapper<>();
        id = new ReadOnlyObjectWrapper<>();
        sender = new ReadOnlyStringWrapper();
        recipients = new ReadOnlyListWrapper<>();
        subject = new ReadOnlyStringWrapper();
        dateSent = new ReadOnlyObjectWrapper<>();
        body = new ReadOnlyStringWrapper();
    }

    public EmailProperty(Email email) {
        this();

        List<String> tmp = email.getRecipients().stream()
                .map(Mailbox::toString)
                .collect(Collectors.toList());


        /*recipients = new ReadOnlyListWrapper<>(FXCollections.observableArrayList(tmp));
        recipients.get().addAll(email.getRecipients());
        subject = new ReadOnlyStringWrapper(email.getSubject());
        dateSent = new ReadOnlyObjectWrapper<>(email.getTimestamp());
        body = new ReadOnlyStringWrapper(email.getBody());*/

        this.email.addListener(((observable, oldMail, newEmail) -> {
            id.set(newEmail.getId());
            sender.set(newEmail.getSender().toString());
            // recipients.setAll(newEmail.getRecipients());
            newEmail.getRecipients().forEach(r -> recipients.add(r.toString()));
            subject.set(email.getSubject());
            dateSent.set(email.getTimestamp());
            body.set(newEmail.getBody());
        }));
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
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

}
