package de.skuzzle.stringz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.skuzzle.stringz.strategy.ControlFactory;

/**
 * This annotation can be used to apply a custom {@link java.util.ResourceBundle} look up
 * strategy. It must only be used on <em>message classes</em> which are also annotated
 * with {@link ResourceMapping}. Using the {@link #value()} attribute, you can specify a 
 * {@link ControlFactory} class which will be instantiated by the Stringz class
 * to retrieve a {@link java.util.ResourceBundle.Control Control} instance to use with
 * {@link java.util.ResourceBundle#getBundle(String, java.util.ResourceBundle.Control)}.
 *  
 * @author Simon Taddiken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceControl {
    /**
     * Specifies the {@link ControlFactory} which will be used to create a 
     * {@link java.util.ResourceBundle.Control Control} instance. That instance will then
     * be used to locate and read a ResourceBundle for this class.
     * @return The class of the {@code ControlFactory} to use.
     */
    Class<? extends ControlFactory> value();
    
    /**
     * Defines optional arguments to be passed to 
     * {@link ControlFactory#create(ResourceMapping, String[])}. Defaults to an
     * empty array.
     * @return Array of additional parameters for the {@code ControlFactory}
     */
    String[] args() default {};
}
