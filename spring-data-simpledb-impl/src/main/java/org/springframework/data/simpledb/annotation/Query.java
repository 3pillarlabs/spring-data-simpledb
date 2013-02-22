package org.springframework.data.simpledb.annotation;

import java.lang.annotation.*;

/**
 * Annotation to declare finder queries directly on repository methods.

 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Query {

    /**
     * Defines the SimpleDb query to be executed when the annotated method is called.
     */
    String value() default "";
    String[] where() default "";
    String[] select() default "";

}
