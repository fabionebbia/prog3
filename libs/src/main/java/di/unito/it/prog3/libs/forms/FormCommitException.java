package di.unito.it.prog3.libs.forms;

class FormCommitException extends Exception {

    FormCommitException(String message, Throwable t) {
        super(message, t);
    }

    FormCommitException(String message) {
        super(message);
    }

}
