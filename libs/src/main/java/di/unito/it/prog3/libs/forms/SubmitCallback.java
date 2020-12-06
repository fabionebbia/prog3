package di.unito.it.prog3.libs.forms;

public interface SubmitCallback<F extends Form> {

    void call(F committedForm);

}
