package di.unito.it.prog3.libs.forms.v2;

public interface SubmitHandler<F extends Form2> {

    void submit(F committedForm);

}
