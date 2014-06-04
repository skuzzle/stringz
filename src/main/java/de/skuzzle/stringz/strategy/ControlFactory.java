package de.skuzzle.stringz.strategy;

import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.Stringz;
import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;

/**
 * Factory interface for {@link java.util.ResourceBundle.Control Control} instances.
 * Implementations of this interface are required to have a no arguments default 
 * constructor if they are to be used with the {@link Stringz} class.
 * 
 * <p>On a class marked with {@link ResourceMapping} you can additionally specify the 
 * {@link ControlFactory} to use for retrieving the 
 * {@link java.util.ResourceBundle ResourceBundle} of that class using the 
 * {@link ResourceControl} annotation. The {@link Stringz} class will then try to 
 * instantiate the provided ControlFactory class and call its 
 * {@link #create(ResourceMapping, String[])} method in order to create a Control 
 * instance. That instance is passed to the ResourceBundle's
 * {@link java.util.ResourceBundle#getBundle(String, java.util.Locale, ClassLoader, Control) getBundle}
 * method.</p>
 * 
 * @author Simon Taddiken
 */
public interface ControlFactory {

    /**
     * Creates a {@link java.util.ResourceBundle.Control} instance which will be used
     * for {@link java.util.ResourceBundle} look up.
     * 
     * @param mapping The ResourceMapping annotation of the message class. Might
     *          especially be used to retrieve the 
     *          {@link ResourceMapping#encoding() encoding} of the bundle to load.
     * @param args Additional arguments from the {@link ResourceControl#args()} attribute.
     * @return The Control for reading the ResourceBundle of a message class.
     * @throws ControlFactoryException If invalid <tt>args</tt> have been provided.
     */
    public Control create(ResourceMapping mapping, String[] args);
}
