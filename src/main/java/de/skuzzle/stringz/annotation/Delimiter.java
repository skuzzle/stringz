package de.skuzzle.stringz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the delimiter for a resource mapped array variable. This annotation
 * only has an effect on {@code String[]} variables.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Delimiter {
    /**
     * The delimiter at which to split the resource's value in order to retrieve an
     * array which is assigned to the annotated field. The delimiter can be any valid
     * regular expression and is used in terms of {@link String#split(String)}.
     *
     * @return The delimiter pattern
     */
    public String value();
}
