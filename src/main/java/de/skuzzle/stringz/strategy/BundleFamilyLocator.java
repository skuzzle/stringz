package de.skuzzle.stringz.strategy;

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
     * 
     * <p>Implementations of this method are not necessarily fail-fast. That is, they
     * are allowed to construct and return a String which is no valid base name for
     * a {@link java.util.ResourceBundle}. In that case, the Stringz class will raise
     * an exception if it can not find the bundle using the name returned by this 
     * method.</p>
     * 
     * @param messages The message class implementation to find the family for.
     * @return The name of the resource bundle family for that class.
     * @throws BundleFamilyLocatorException Can be thrown to indicate malicious 
     *          <tt>BundleFamilyLocator</tt> usage or configuration. 
     */
    public String locateBundleFamily(Class<?> messages);
}
