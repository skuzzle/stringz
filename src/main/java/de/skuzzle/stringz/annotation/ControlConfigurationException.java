package de.skuzzle.stringz.annotation;

import de.skuzzle.stringz.StringzRuntimeException;

/**
 * This exception can be thrown by {@link ControlConfigurator} instances if
 * insufficient parameters are provided.
 * 
 * @author Simon Taddiken
 */
public class ControlConfigurationException extends StringzRuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new Exception with the provided error message.
     * 
     * @param message The error message.
     */
    public ControlConfigurationException(String message) {
        super(message);
    }

    /**
     * Creates a new Exception with the provided message and cause.
     * 
     * @param message The error message.
     * @param cause The cause.
     */
    public ControlConfigurationException(String message, Exception cause) {
        super(message, cause);
    }
}
