package de.skuzzle.stringz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that an array field marked with this annotation is mapped to an
 * array of the provided resource names. This annotation will only have effects
 * on arrays and will be ignored on other fields.
 *
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceCollection {
    /**
     * Specifies the names of resources which values should be assigned to the
     * annotated field. For each name, the default value retrieval is applied.
     * That is, expansion of <code>${...}</code> expressions within resource
     * values is supported for each referenced name.
     *
     * @return Array of valid resource names.
     */
    public String[] value();
}
