package exception;

/**
 * InvalidCustomObjectException is an exception for anything wrong happens in the custom object parsing
 */
public class InvalidCustomObjectException extends RuntimeException {
    /**
     * Throw {@link InvalidCustomObjectException} without any attachments
     */
    public InvalidCustomObjectException() {
    }

    /**
     * Throw {@link InvalidCustomObjectException} with custom message
     *
     * @param message exception message
     */
    public InvalidCustomObjectException(String message) {
        super(message);
    }

    /**
     * Throw {@link InvalidCustomObjectException} with custom message and the cause
     *
     * @param message exception message
     * @param cause   the cause of the exception
     */
    public InvalidCustomObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw {@link InvalidCustomObjectException} with the cause
     *
     * @param cause the cause of the exception
     */
    public InvalidCustomObjectException(Throwable cause) {
        super(cause);
    }
}
