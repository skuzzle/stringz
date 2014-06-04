package de.skuzzle.stringz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ResourceBundle;

import de.skuzzle.stringz.Stringz;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperFactory;

/**
 * This annotation can be used to specify custom strategies of how fields are mapped to
 * values from a {@link ResourceBundle}. It must only be used on <em>message classes</em>
 * which are also annotated with {@link ResourceMapping}. Using the {@link #value()}
 * attribute, you can specify a {@link FieldMapperFactory} class which will
 * be instantiated by the {@link Stringz} class to retrieve a {@link FieldMapper} 
 * instance.
 * 
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FieldMapping {
    /**
     * Specifies the {@link FieldMapperFactory} to use for this message class. 
     * @return The {@link FieldMapperFactory} instance which provides a 
     *          {@link FieldMapper} instance for this class.
     */
    Class<? extends FieldMapperFactory> value();
    
    /**
     * Defines optional arguments to be passed to 
     * {@link FieldMapperFactory#create(ResourceMapping, String[])}. Defaults to 
     * an empty array.
     * @return Array of additional parameters for the <tt>FieldMapper</tt>
     */
    String[] args() default {};
}
