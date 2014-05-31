package de.skuzzle.stringz;

import java.util.Locale;

/**
 * Provides parameters for {@link java.util.ResourceBundle ResourceBundle}
 * lookup and IO. Instances of this interface can either be set globally for the
 * {@link Stringz} class or can be passed to the proper overloaded <tt>init</tt>
 * method of the <tt>Stringz</tt> class.
 * 
 * @author Simon Taddiken
 * @see Stringz
 */
public interface StringzConfiguration {

    /**
     * Gets the name of the charset in which a bundle is stored. If this is no
     * valid charset name, an exception will be thrown by either of the
     * {@link Stringz}' <tt>init</tt> method if this configuration is used.
     * 
     * @return The charset name.
     */
    public String getBundleCharsetName();

    /**
     * Gets the locale which should be used to retrieve a bundle.
     * 
     * @return The locale to use.
     */
    public Locale getLocale();
}
