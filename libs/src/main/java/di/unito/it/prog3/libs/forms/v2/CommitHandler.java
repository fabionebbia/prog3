package di.unito.it.prog3.libs.forms.v2;

public interface CommitHandler<F extends Form2> {

    void committed(F committedForm);

}
