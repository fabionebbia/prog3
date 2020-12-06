package di.unito.it.prog3.libs.model;

import javafx.beans.property.ReadOnlyBooleanProperty;

interface ConstrainedProperty{

    ReadOnlyBooleanProperty isValid();

    void addValidationListener(ValidationListener listener);

    void removeValidationListener(ValidationListener listener);

}
