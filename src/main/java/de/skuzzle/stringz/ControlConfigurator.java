package de.skuzzle.stringz;

import java.util.ResourceBundle.Control;

/**
 * Factory interface for {@link java.util.ResourceBundle.Control Control} instances.
 * Implementations of this interface are required to have a no arguments default 
 * constructor if they are to be used with the {@link Stringz} class.
 * 
 * <p>On a class marked with {@link ResourceMapping} you can additionally specify the 
 * {@link ControlConfigurator} to use for retrieving the 
 * {@link java.util.ResourceBundle ResourceBundle} of that class using the 
 * {@link ResourceControl} annotation. The {@link Stringz} class will then try to 
 * instantiate the provided ControlConfigurator class and call its 
 * {@link #configure(ResourceMapping, String[])} method in order to create a Control 
 * instance. That instance is passed to the ResourceBundle's
 * {@link java.util.ResourceBundle#getBundle(String, java.util.Locale, ClassLoader, Control) getBundle}
 * method.</p>
 * 
 * @author Simon Taddiken
 */
public interface ControlConfigurator {

    /**
     * Creates a {@link java.util.ResourceBundle.Control} instance which will be used
     * for {@link java.util.ResourceBundle} look up.
     * 
     * @param mapping The ResourceMapping annotation of the message class. Might
     *          especially be used to retrieve the 
     *          {@link ResourceMapping#encoding() encoding} of the bundle to load.
     * @param args Additional arguments from the {@link ResourceControl#args()} attribute.
     * @return The Control for reading the ResourceBundle of a message class.
     */
    public Control configure(ResourceMapping mapping, String[] args);
}
