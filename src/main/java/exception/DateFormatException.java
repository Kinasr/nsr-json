package exception;

/**
 * DateFormatException is an exception for unsupported date formats
 */
public class DateFormatException extends RuntimeException {
    /**
     * Throw {@link DateFormatException} without any attachments
     */
    public DateFormatException() {
    }

    /**
     * Throw {@link DateFormatException} with custom message
     *
     * @param message exception message
     */
    public DateFormatException(String message) {
        super(message);
    }

    /**
     * Throw {@link DateFormatException} with custom message and the cause
     *
     * @param message exception message
     * @param cause   the cause of the exception
     */
    public DateFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw {@link DateFormatException} with the cause
     *
     * @param cause the cause of the exception
     */
    public DateFormatException(Throwable cause) {
        super(cause);
    }
}
