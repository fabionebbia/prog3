package di.unito.it.prog3.client.controllers;

import di.unito.it.prog3.client.controls.Recipient;
import di.unito.it.prog3.client.fxml.BaseController;
import di.unito.it.prog3.client.fxml.ScreenManager;
import di.unito.it.prog3.client.model.Model;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class EmailViewerController extends BaseController<Model> {

    @FXML
    public FlowPane recipients;

    @FXML
    private TextField recipientField;

    public EmailViewerController(Model model) {
        init(null, model);
    }


    @Override
    public void init(ScreenManager screenManager, Model model) {
        super.init(screenManager, model);

        /*recipientsListView.getItems().addAll("abc", "cde");
        recipientsListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new RecipientListCell();
            }
        });

        recipientsListView.prefHeightProperty().bind(
                Bindings.multiply(Bindings.size(recipientsListView.getItems()), 40));
        recipientsListView.minHeightProperty().bind(recipientsListView.prefHeightProperty());
        recipientsListView.maxHeightProperty().bind(recipientsListView.prefHeightProperty());*/

        model.currentEmailProperty().recipientsProperty().addListener((ListChangeListener<String>) c -> {
            if (c.next() && c.wasAdded()) {
                for (String addedRecipient : c.getAddedSubList()) {
                    loadRecipient(addedRecipient);
                }
            }
        });
    }

    private void loadRecipient(String addedRecipient) {
        /*try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recipient.fxml"));
            loader.setController(this);
            loader.setRoot(new HBox());
            Parent newRecipient = loader.load();
            recipients.getChildren().add(newRecipient);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            Recipient recipient = new Recipient(addedRecipient);
            recipients.getChildren().add(recipient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRecipient(String recipient) {
        model.currentEmailProperty().recipientsProperty().add(recipient);
    }

    public void addRecipient(ActionEvent e) {
        // TODO e il binding? :(
        addRecipient(recipientField.getText());
    }

    public void removeRecipient(ActionEvent e) {

    }

}
