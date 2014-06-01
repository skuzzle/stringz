package de.skuzzle.stringz;

public class ControlConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    
    public ControlConfigurationException(String message) {
        super(message);
    }
    
    
    public ControlConfigurationException(String message, Exception cause) {
        super(message, cause);
    }
}
