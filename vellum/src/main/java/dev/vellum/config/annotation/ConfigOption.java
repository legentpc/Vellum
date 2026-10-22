package dev.vellum.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOption {
    String name();

    String description() default "";

    String category() default "general";

    String defaultValue() default "";

    double min() default -Double.MAX_VALUE;

    double max() default Double.MAX_VALUE;

    double step() default 1.0D;
}
