package org.github.kshashov.uifields.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface UIField {
    String enumProperty() default "";
    String title() default "";
    String caption() default "";
}
