package di.unito.it.prog3.client.forms;

import di.unito.it.prog3.libs.forms.Form;
import di.unito.it.prog3.libs.forms.v2.Form2;
import di.unito.it.prog3.libs.forms.v2.annotations.Bounded;
import di.unito.it.prog3.libs.forms.v2.annotations.Email;


public class LoginForm implements Form<LoginForm.Field>, Form2 {

    private String server;

    @Bounded(min = 1, max = 65535)
    private int port;

    @Email
    private String email;


    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getEmail() {
        return email;
    }

    public enum Field {
        SERVER, PORT, EMAIL
    }

}