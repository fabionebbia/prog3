package di.unito.it.prog3.client.controls;

import com.sun.javafx.geom.Rectangle;
import di.unito.it.prog3.libs.email.Email;
import di.unito.it.prog3.libs.utils.CssUtils;
import di.unito.it.prog3.libs.utils.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class EmailPreview extends ListCell<Email> implements EventHandler<MouseEvent> {

    private final DateTimeFormatter TODAY = DateTimeFormatter.ofPattern("H:mm");
    private final DateTimeFormatter THIS_YEAR = DateTimeFormatter.ofPattern("d MMM");
    private final DateTimeFormatter PREVIOUS_YEAR = DateTimeFormatter.ofPattern("d MMM y");

    @FXML @SuppressWarnings("unused")
    private Label fromLabel;

    @FXML @SuppressWarnings("unused")
    private Label datetimeLabel;

    @FXML @SuppressWarnings("unused")
    private Label subjectLabel;

    @FXML @SuppressWarnings("unused")
    private Label bodyLabel;

    @FXML
    private Label reLabel;

    private final GridPane graphic;


    private final BooleanProperty preview;
    private Email email;



    public EmailPreview() {
        try {
            /*
                TODO which folder should this fxml file be located in?
                TODO And what about this class?
             */
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controls/email-preview.fxml"));
            loader.setController(this);
            graphic = loader.load();

            setOnMouseClicked(this);
            preview = new SimpleBooleanProperty(true);

            Utils.bindVisibility(preview.not(), reLabel);
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

        /*ScaleTransition st = new ScaleTransition(Duration.millis(2000), graphic);
        st.setByX(1f);
        st.setByY(1.5f);
        st.setCycleCount(4);
        st.setAutoReverse(true);

        st.play();*/
/*
        KeyValue widthValue = new KeyValue(graphic.heightProperty(), graphic.getHeight() + 100);
        KeyFrame frame = new KeyFrame(Duration.seconds(2), widthValue);
        Timeline timeline = new Timeline(frame);
        timeline.play();
        timeline.setOnFinished(finishedEvent -> System.out.println("Animation end: width = " );*/
    }



    private void update(Email email) {
        this.email = email;

        CssUtils.ensureClassSet(graphic, "email-preview");
        CssUtils.ensureClassSetOnlyIf(graphic, "email-preview--unread", email.isUnread());

        // Prevents horizontal overflow
        graphic.maxWidthProperty().bind(widthProperty());

        // replaces new lines with double space
        // (\\R pattern is platform independent and matches new lines)
        String compactBody = email.getBody().replaceAll("\\R", "  ");
        String formattedSentDate = formatTimestap(email);

        datetimeLabel.setText(formattedSentDate);
        subjectLabel.setText(email.getSubject());
        fromLabel.setText(email.getSender().toString());
        bodyLabel.setText(compactBody);

       // Utils.setVisibility(email.hasReplies(), reLabel);
        if (email.hasReplies()) {
            reLabel.setText("RE: " + email.getSubject());
        }
    }

    @Override
    public void handle(MouseEvent event) {

    }


    private String formatTimestap(Email email) {
        LocalDateTime timestamp = email.getTimestamp();
        DateTimeFormatter formatter = PREVIOUS_YEAR;

        if (sentToday(timestamp)) {
            formatter = TODAY;
        } else if (sentThisYear(timestamp)) {
            formatter = THIS_YEAR;
        }

        return timestamp.format(formatter);
    }

    private boolean sentToday(LocalDateTime timestamp) {
        return LocalDate.now().equals(timestamp.toLocalDate());
    }

    private boolean sentThisYear(LocalDateTime timestamp) {
        return Year.now().getValue() == timestamp.getYear();
    }
}
