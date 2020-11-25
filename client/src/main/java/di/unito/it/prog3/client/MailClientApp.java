package di.unito.it.prog3.client;

import di.unito.it.prog3.client.fxml.ScreenManager;
import di.unito.it.prog3.client.model.Client;
import di.unito.it.prog3.client.model.Model;
import di.unito.it.prog3.client.views.ClientScreen;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;

import static di.unito.it.prog3.libs.utils.ExecutionMode.DEBUG;

public class MailClientApp extends Application {

    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (DEBUG) {
            // TODO usarla. perchè l'ho messa actually?
        }
        client = new Client();

        Map<String, String> parameters = getParameters().getNamed();
        String username = parameters.get("user");
        String server = parameters.get("server");

        Model model = new Model(client, server, username);

        ScreenManager screenManager = new ScreenManager(primaryStage, getClass());
        screenManager.loadAndSet(ClientScreen.LOGIN, model);
        screenManager.show();
        client.start();

        String automaticLogin = parameters.getOrDefault("automatic-login", "false");
        if (Boolean.parseBoolean(automaticLogin)) {
            model.login();
        }
    }

    @Override
    public void stop() throws Exception {
        client.stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);

        /*
        Email e = new EmailBuilder("a@b.c/R/11375")
                .setSender("fabiana.vernero@unito.it")
                .addRecipient("fabionebbia@edu.unito.it")
                .setSentDate(new Date())
                .setSubject("Fragole e asparagi")
                .setBody("Buongiorno!\nE benvenuti alla terza lezione..")
                .build();


        try {
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            String json = om.writeValueAsString(e);
            System.out.println(json);

            Email eee = om.readValue(json, Email.class);
            System.out.println(eee);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        EmailStore store = new FileBasedEmailStoreClient("_store", ".txt");
        try {
            store.store(e);
        } catch (EmailStoreException ex) {
            ex.printStackTrace();
        }

        try {
            Thread.sleep(7000);
            //store.delete(e.getID());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }*/
    }
}
