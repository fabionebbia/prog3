package di.unito.it.prog3.libs.forms.v2.annotations;

import di.unito.it.prog3.libs.forms.v2.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bounded {

    int min() default Integer.MIN_VALUE;

    int max() default Integer.MAX_VALUE;

}
