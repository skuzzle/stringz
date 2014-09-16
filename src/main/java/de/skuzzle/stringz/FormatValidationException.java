package de.skuzzle.stringz;

public class FormatValidationException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public FormatValidationException() {}

    public FormatValidationException(String message) {
        super(message);
    }
}
