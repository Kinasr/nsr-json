package exception;

/**
 * NotAListException is an exception for objects that can not be parsed to a list
 */
public class NotAListException extends RuntimeException {

    /**
     * Throw {@link NotAListException} without any attachments
     */
    public NotAListException() {
    }

    /**
     * Throw {@link NotAListException} with custom message
     *
     * @param message exception message
     */
    public NotAListException(String message) {
        super(message);
    }

    /**
     * Throw {@link NotAListException} with custom message and the cause
     *
     * @param message exception message
     * @param cause   the cause of the exception
     */
    public NotAListException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw {@link NotAListException} with the cause
     *
     * @param cause the cause of the exception
     */
    public NotAListException(Throwable cause) {
        super(cause);
    }
}
