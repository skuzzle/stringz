package de.skuzzle.stringz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When using the default field mapping strategy, this annotation can be used to 
 * explicitly specify the resource key to which a variable should be mapped. If this 
 * annotation is not present on a field, its name is used as key.
 *  
 * @author Simon Taddiken
 * @see DefaultFieldMapper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceKey {
    /**
     * Specifies the key which references the value within a 
     * {@link java.util.ResourceBundle} taht should be assigned to the annotated field.
     * @return The resource key to use.
     */
    public String value();
}
