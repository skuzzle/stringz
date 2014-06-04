package de.skuzzle.stringz.strategy;

import de.skuzzle.stringz.StringzRuntimeException;

/**
 * This exception can be thrown by {@link BundleFamilyLocator
 * BundleFamilyLocators} when they fail to find the proper base name for a
 * specified class.
 * 
 * @author Simon Taddiken
 */
public class BundleFamilyException extends StringzRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new BundleFamilyLocatorException with the provided message.
     * 
     * @param message The message.
     */
    public BundleFamilyException(String message) {
        super(message);
    }
    
    /**
     * Creates a new BundleFamilyLocatorException with the provided message and cause.
     * @param message The message.
     * @param cause The cause.
     */
    public BundleFamilyException(String message, Exception cause) {
        super(message, cause);
    }
}
