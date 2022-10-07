package exception;

/**
 * NotAMapException is an exception for objects that can not be parsed to a map
 */
public class NotAMapException extends RuntimeException {

    /**
     * Throw {@link NotAMapException} without any attachments
     */
    public NotAMapException() {
    }

    /**
     * Throw {@link NotAMapException} with custom message
     *
     * @param message exception message
     */
    public NotAMapException(String message) {
        super(message);
    }

    /**
     * Throw {@link NotAMapException} with custom message and the cause
     *
     * @param message exception message
     * @param cause   the cause of the exception
     */
    public NotAMapException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw {@link NotAMapException} with the cause
     *
     * @param cause the cause of the exception
     */
    public NotAMapException(Throwable cause) {
        super(cause);
    }
}
