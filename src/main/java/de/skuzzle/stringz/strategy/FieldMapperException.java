package de.skuzzle.stringz.strategy;

public class FieldMapperException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FieldMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public FieldMapperException(String message) {
        super(message);
    }

    public FieldMapperException(Throwable cause) {
        super(cause);
    }
}
