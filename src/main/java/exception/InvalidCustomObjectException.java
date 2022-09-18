package exception;

public class InvalidCustomObjectException extends RuntimeException{
    public InvalidCustomObjectException() {
    }

    public InvalidCustomObjectException(String message) {
        super(message);
    }

    public InvalidCustomObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCustomObjectException(Throwable cause) {
        super(cause);
    }
}
