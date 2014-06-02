package de.skuzzle.stringz;

public class FieldMappingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FieldMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldMappingException(String message) {
        super(message);
    }

    public FieldMappingException(Throwable cause) {
        super(cause);
    }
}
