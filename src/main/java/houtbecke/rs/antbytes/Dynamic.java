package houtbecke.rs.antbytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.FIELD})
public @interface Dynamic {
    int value();
    int order();
    int startByte() default 0;
    boolean inverse() default false;

}
