package di.unito.it.prog3.client.trashed;

import di.unito.it.prog3.client.forms.LoginForm;
import di.unito.it.prog3.client.screen.Controller;
import di.unito.it.prog3.libs.net.ResponseHandler;
import di.unito.it.prog3.libs.net.Response;
import di.unito.it.prog3.libs.forms.v2.CommitHandler;
import di.unito.it.prog3.libs.forms.v2.FormField2;
import di.unito.it.prog3.libs.forms.v2.FormManager2;
import di.unito.it.prog3.libs.model.Error;
import di.unito.it.prog3.libs.utils.Callback;
import di.unito.it.prog3.libs.utils.Perform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginScreenController extends Controller implements CommitHandler<LoginForm> {

    private FormManager2<LoginForm> form;
    private Callback successCallback;

    @FXML private TextField serverField;
    @FXML private TextField portField;
    @FXML private TextField emailField;

    @FXML private Label serverFieldError;
    @FXML private Label portFieldError;
    @FXML private Label emailFieldError;

    @FXML private Button submitButton;


    @Override
    protected void setupControl() {
        form = new FormManager2<>(LoginForm.class, this);
        FormField2 server = form.register(LoginForm.Field.SERVER, serverField);
        FormField2 port = form.register(LoginForm.Field.PORT, portField);
        FormField2 email = form.register(LoginForm.Field.EMAIL, emailField);

        if (Boolean.parseBoolean(System.getProperty("debug"))) {
            server.set(System.getProperty("server"));
            port.set(System.getProperty("port"));
            email.set(System.getProperty("email"));
        }

        server.attachErrorLabel(serverFieldError);
        port.attachErrorLabel(portFieldError);
        email.attachErrorLabel(emailFieldError);

        // force integers only on port TextField
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith("0")) {
                portField.setText(newValue.substring(1));
            } else if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        form.setSubmitControl(submitButton);
        form.computeFocus();
    }

    public void submit(Event e) {
        if (e instanceof KeyEvent && ((KeyEvent) e).getCode() != KeyCode.ENTER) {
            return;
        }
        form.commit();
    }

    @Override
    public void committed(LoginForm lf) {
        String server = lf.getServer();
        int port = lf.getPort();
        String user = lf.getEmail();

       /* Perform.async(() -> model.login(server, port, user), response -> {
            if (response.successful()) {
                if (successCallback != null) {
                    successCallback.call();
                }
            } else {
                Error.display("Login error", "Could not login", response);
            }
        });*/
    }

    public void onSuccessfulLogin(Callback successCallback) { // TODO check already set
        this.successCallback = successCallback;
    }

}
