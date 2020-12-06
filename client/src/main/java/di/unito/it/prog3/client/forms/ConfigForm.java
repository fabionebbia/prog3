package di.unito.it.prog3.client.forms;

import di.unito.it.prog3.libs.forms.v2.annotations.Bounded;

public class ConfigForm {

    @Bounded(min = 1)
    private int pollingInterval;

}
