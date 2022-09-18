package exception;

public class NotAMapException extends RuntimeException{
    public NotAMapException() {
    }

    public NotAMapException(String message) {
        super(message);
    }

    public NotAMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAMapException(Throwable cause) {
        super(cause);
    }
}
