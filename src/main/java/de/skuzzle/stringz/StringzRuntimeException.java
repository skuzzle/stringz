package de.skuzzle.stringz;

/**
 * Superclass for all Exceptions which can be thrown bz the strategz interfaces of the
 * Stringy framework.
 * 
 *  * @author Simon Taddiken
 */
public class StringzRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StringzRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringzRuntimeException(String message) {
        super(message);
    }

    public StringzRuntimeException(Throwable cause) {
        super(cause);
    }
}
