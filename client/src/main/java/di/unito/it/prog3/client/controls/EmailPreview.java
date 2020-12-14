package di.unito.it.prog3.client.controls;

import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.CssUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

// Oracle - Creating a Custom Control with FXML
// url: https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm
public class EmailPreview extends ListCell<Email> {

    private final DateTimeFormatter TODAY = DateTimeFormatter.ofPattern("H:mm");
    private final DateTimeFormatter THIS_YEAR = DateTimeFormatter.ofPattern("d MMM");
    private final DateTimeFormatter PREVIOUS_YEAR = DateTimeFormatter.ofPattern("d MMM y");

    private final GridPane graphic;

    @FXML @SuppressWarnings("unused")
    private Label fromLabel;

    @FXML @SuppressWarnings("unused")
    private Label datetimeLabel;

    @FXML @SuppressWarnings("unused")
    private Label subjectLabel;

    @FXML @SuppressWarnings("unused")
    private Label bodyLabel;


    public EmailPreview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls/email-preview.fxml"));
            loader.setController(this);
            graphic = loader.load();

            // Prevents horizontal overflow
            graphic.maxWidthProperty().bind(widthProperty());
        } catch (IOException e) {
            throw new RuntimeException("Could not load email preview", e);
        }
    }


    @Override
    protected void updateItem(Email email, boolean empty) {
        super.updateItem(email, empty);
        if (empty || email == null) {
            setGraphic(null);
        } else {
            update(email);
            setGraphic(graphic);
        }
    }


    /**
     * Updates the preview.
     *
     * @param email The previewed e-mail.
     */
    private void update(Email email) {
        // Ensure preview reflect the read/unread status
        CssUtils.ensureClassSet(graphic, "email-preview");
        CssUtils.ensureClassSetOnlyIf(graphic, "email-preview--unread", email.isUnread());

        // replaces new lines with double space
        // (\\R pattern is platform independent and matches new lines)
        String compactBody = email.getBody().replaceAll("\\R", "  ");
        String formattedSentDate = formatTimestamp(email);

        // Show default subject if subject is empty
        String subject = email.getSubject();
        subject = (subject != null && !subject.isBlank() ? subject : "-No Subject-");

        // Set the properties
        datetimeLabel.setText(formattedSentDate);
        fromLabel.setText(email.getSender());
        bodyLabel.setText(compactBody);
        subjectLabel.setText(subject);
    }


    /**
     * Formats the e-mail timestamp for user-friendliness.
     *
     * @param email The e-mail whose timestamp must be formatted.
     * @return The formatted timestamp.
     */
    private String formatTimestamp(Email email) {
        LocalDateTime timestamp = email.getTimestamp();
        DateTimeFormatter formatter = PREVIOUS_YEAR;

        if (wasSentToday(timestamp)) {
            formatter = TODAY;
        } else if (wasSentThisYear(timestamp)) {
            formatter = THIS_YEAR;
        }

        return timestamp.format(formatter);
    }


    /**
     * Checks whether the e-mail was sent today.
     *
     * @param timestamp The e-mail timestamp.
     * @return True if the e-mail was sent today, false otherwise.
     */
    private boolean wasSentToday(LocalDateTime timestamp) {
        return LocalDate.now().equals(timestamp.toLocalDate());
    }


    /**
     * Checks whether the e-mail was sent the current year.
     *
     * @param timestamp The e-mail timestamp.
     * @return True if the e-mail was sent the current year, false otherwise.
     */
    private boolean wasSentThisYear(LocalDateTime timestamp) {
        return Year.now().getValue() == timestamp.getYear();
    }

}
