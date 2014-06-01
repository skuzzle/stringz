package de.skuzzle.stringz;

/**
 * Strategy interface for finding the bundle family to use with a certain 
 * {@link ResourceMapping}. 
 * <a href="http://docs.oracle.com/javase/tutorial/i18n/resbundle/index.html">See the 
 * Oracle Online Trail about Resource Bundles</a> to learn about resource bundle families.
 * 
 * <p>Stringz provides some default strategies to locate the bundle family. Refer to 
 * {@link ResourceMapping} and {@link Stringz} documentation for more information.</p>
 * @author Simon Taddiken
 */
public interface BundleFamilyLocator {

    /**
     * Given a message class which is marked as {@link ResourceMapping}, this method 
     * returns the resource family to use for locating the resource which belongs to that 
     * implementation.
     * @param messages The message class implementation to find the family for.
     * @return The name of the resource bundle family for that class.
     */
    public String locateBundleFamily(Class<?> messages);
}
