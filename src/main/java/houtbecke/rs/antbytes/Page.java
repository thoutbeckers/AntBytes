package houtbecke.rs.antbytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ ElementType.FIELD})
public @interface Page {
    /**
     * The page id of an Ant message
     */
    public int value() default -1;
}
