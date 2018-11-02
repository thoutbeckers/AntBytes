package houtbecke.rs.antbytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.FIELD})
public @interface ValuesArray {
    // Expected number of elements.
    // When 0 - data will be parsed into the field till the end of byte array.
    int value() default 0;
}
