package houtbecke.rs.antbytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.FIELD})
public @interface SXBIT {
    int value() default 0;
    int startBit() default 0;
    int bitLength() default 1;
}
