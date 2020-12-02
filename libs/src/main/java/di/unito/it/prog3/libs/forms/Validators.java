package di.unito.it.prog3.libs.forms;

import di.unito.it.prog3.libs.utils.Emails;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public interface Validators {

    Validator<String> _ANY_STRING = new StringValidator(s -> true);

    Validator<String> _NON_BLANK_STRING = new NonBlankStringValidator(s -> true);

    Validator<String> _EMAIL = new NonBlankStringValidator(Emails::isWellFormed);

    Validator<Integer> _INTEGER = new Validator<>(Validators::toInteger, s -> true);

    Validator<Integer> _PORT = new Validator<>(Validators::toInteger, v -> 1 <= v && v <= 65535);


    private static String isNonBlank(String s) {
        if (s == null || s.isBlank()) throw new FormException("Field is blank");
        else return s;
    }

    private static int toInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new FormException("Field value " + s + " is not an integer");
        }
    }

    private static int isPort(int value) {
        if (1 <= value && value <= 65535) return value;
        else throw new RuntimeException("Field value " + value + " is not a valid port number");
    }

    private static String wellFormedEmail(String s) {
        if (!Emails.isWellFormed(s)) throw new FormException("Invalid email address " + s);
        else return s;
    }


    Predicate<String> p = new Predicate<String>() {
        @Override
        public boolean test(String s) {
            return false;
        }
    } ;

    BooleanBinding b = Bindings.createBooleanBinding(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return null;
        }
    });

    interface Extractor<R> {
        R extract(String text);
    }

    class Validator<R> extends BooleanBinding {
        private final Extractor<R> extractor;
        private final Predicate<R> predicate;
        private ObservableValue<String> observable;
        private Form.Key<?> key;
        private R validated;

        public Validator(Extractor<R> extractor, Predicate<R> predicate) {
            this.extractor = extractor;
            this.predicate = predicate;
        }

        void attachTo(Form.Key<?> key, ObservableValue<String> observable) {
            this.key = key;
            this.observable = observable;

            bind(observable);
        }

        R getValidated() {
            if (!get()) {
                throw new IllegalStateException("Trying to get validated value from invalid field " + key);
            }
            return validated;
        }

        @Override
        protected boolean computeValue() {
            String text = observable.getValue();
            R value = extractor.extract(text);
            boolean valid = predicate.test(value);

            if (valid) {
                validated = value;
            }

            return valid;
        }
    }

    class StringValidator extends Validator<String> {
        public StringValidator(Predicate<String> predicate) {
            super(s -> s, predicate);
        }
    }

    class NonBlankStringValidator extends Validator<String> {
        public NonBlankStringValidator(Predicate<String> predicate) {
            super(s -> s, s -> s != null && !s.isBlank() && predicate.test(s));
        }
    }

}
