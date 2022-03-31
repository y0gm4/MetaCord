package org.metacord.ds.commandPattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
    //executes the annotated method when a user submits this string
    String executeOnCall() default "";

    //it is the description of the command that is saved in a HashMap accessible via CommandData<Command>#helpDescription()
    String description() default "";
}
