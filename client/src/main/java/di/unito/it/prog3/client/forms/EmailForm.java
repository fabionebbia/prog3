package di.unito.it.prog3.client.forms;

import di.unito.it.prog3.libs.forms.v2.annotations.Email;
import di.unito.it.prog3.libs.forms.v2.annotations.Optional;

import java.util.List;

public class EmailForm {

    @Email
    private List<String> recipients;

    @Optional
    private String subject;

    @Optional
    private String body;

}
