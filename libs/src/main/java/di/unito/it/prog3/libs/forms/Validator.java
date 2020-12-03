package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.forms.FormException.InvalidValueException;

public interface Validator<V> {

    V validate(String input) throws InvalidValueException;

}
