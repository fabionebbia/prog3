package di.unito.it.prog3.libs.forms.v2;

public interface Constraint {

    boolean test(String input);


    interface ConstraintCheck {
        boolean succeeded();
        String getMessage();
    }


    ConstraintCheck SUCCESS = new ConstraintCheck() {
        @Override public boolean succeeded() { return true; }
        @Override public String getMessage() { return "";   }
    };


    class FAILURE implements ConstraintCheck {
        private final String message;
        public FAILURE(String message) { this.message = message; }
        @Override public boolean succeeded() { return false;   }
        @Override public String getMessage() { return message; }
    }

}
