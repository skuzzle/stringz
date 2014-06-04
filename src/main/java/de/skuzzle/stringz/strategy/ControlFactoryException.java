package de.skuzzle.stringz.strategy;

import de.skuzzle.stringz.StringzRuntimeException;

/**
 * This exception can be thrown by {@link ControlFactory} instances if
 * insufficient parameters are provided.
 * 
 * @author Simon Taddiken
 */
public class ControlFactoryException extends StringzRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new Exception with the provided error message.
     * 
     * @param message The error message.
     */
    public ControlFactoryException(String message) {
        super(message);
    }

    /**
     * Creates a new Exception with the provided message and cause.
     * 
     * @param message The error message.
     * @param cause The cause.
     */
    public ControlFactoryException(String message, Exception cause) {
        super(message, cause);
    }
}
