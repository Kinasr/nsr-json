package exception;

/**
 * JSONFileException is an exception for anything wrong related to JSON file parsing
 */
public class JSONFileException extends RuntimeException {
    /**
     * Throw {@link JSONFileException} without any attachments
     */
    public JSONFileException() {
    }

    /**
     * Throw {@link JSONFileException} with custom message
     *
     * @param message exception message
     */
    public JSONFileException(String message) {
        super(message);
    }

    /**
     * Throw {@link JSONFileException} with custom message and the cause
     *
     * @param message exception message
     * @param cause   the cause of the exception
     */
    public JSONFileException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw {@link JSONFileException} with the cause
     *
     * @param cause the cause of the exception
     */
    public JSONFileException(Throwable cause) {
        super(cause);
    }
}
