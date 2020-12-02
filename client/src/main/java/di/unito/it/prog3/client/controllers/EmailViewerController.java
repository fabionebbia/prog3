package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.client.controls.RecipientsFlowPane;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;

public class EmailViewerController extends Controller {

    @FXML
    private RecipientsFlowPane recipientsFlowPane;

    @FXML
    private Label fromIndicator;

    @FXML
    private TextField fromField;

    @FXML
    private TextField subjectField;

    @FXML
    private Label dateIndicator;

    @FXML
    private Label dateLabel;

    @FXML
    private GridPane gridPane;


    private final BooleanProperty modifiable;
    private final int FROM_ROW = 1;
    private final int DATE_ROW = 3;

    public EmailViewerController() {
        modifiable = new SimpleBooleanProperty(true);
    }

    @Override
    public void setupControl() {
        recipientsFlowPane.recipientsProperty().bindBidirectional(model.currentEmailProperty().recipientsProperty());

        fromField.textProperty().bindBidirectional(model.currentEmailProperty().fromProperty());
        fromField.disableProperty().bind(Bindings.not(modifiable));
        bindVisibility(fromIndicator, fromField);

        subjectField.disableProperty().bind(Bindings.not(modifiable));

        bindVisibility(dateIndicator, dateLabel);

        modifiable.addListener(((observable, wasModifiable, isModifiable) -> {
            ObservableList<RowConstraints> rowConstraints = gridPane.getRowConstraints();
            double newMaxHeight = isModifiable ? Control.USE_COMPUTED_SIZE : 0;

            rowConstraints.get(DATE_ROW).setMaxHeight(newMaxHeight);
            rowConstraints.get(FROM_ROW).setMaxHeight(newMaxHeight);

            // TODO vedere se posso usare i CSS invece che sta cosa esplicita
            if (isModifiable) {
                gridPane.setVgap(gridPane.getVgap() * 2);
            } else {
                gridPane.setVgap(gridPane.getVgap() / 2);
            }
        }));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(6), e ->
                        modifiable.set(!modifiable.get())
                )
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();


        /*
            TODO
            - [ ] rendere modificabile/non modificabile anche RecipientsFlowPane
            - [ ] usare i CSS per modificare i gap
            - [ ] recipient.css è importato sia da recipient.fxml che da recipient-flow-pane.fxml
            - [ ] fare in modo che, quando disabilitato, subjectField appaia come una label (CSS)
            - [ ] i bottoni della toolbar non sono più stylati correttamente
         */
    }

    private void bindVisibility(Node... nodes) {
        for (Node node : nodes) {
            node.managedProperty().bind(modifiable); // removes the node from the parent's layout calculation
            node.visibleProperty().bind(modifiable);
        }
    }

}
