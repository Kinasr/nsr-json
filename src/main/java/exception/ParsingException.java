package exception;

public class ParsingException extends RuntimeException{

    /**
     * Throw {@link ParsingException} without any attachments
     */
    public ParsingException() {
    }

    /**
     * Throw {@link ParsingException} with custom message
     *
     * @param message exception message
     */
    public ParsingException(String message) {
        super(message);
    }

    /**
     * Throw {@link ParsingException} with custom message and the cause
     *
     * @param message exception message
     * @param cause   the cause of the exception
     */
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw {@link ParsingException} with the cause
     *
     * @param cause the cause of the exception
     */
    public ParsingException(Throwable cause) {
        super(cause);
    }
}
