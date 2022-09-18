package exception;

public class NotAListException extends RuntimeException{
    public NotAListException() {
    }

    public NotAListException(String message) {
        super(message);
    }

    public NotAListException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAListException(Throwable cause) {
        super(cause);
    }
}
