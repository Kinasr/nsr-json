package exception;

public class JSONFileException extends RuntimeException{
    public JSONFileException() {
    }

    public JSONFileException(String message) {
        super(message);
    }

    public JSONFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONFileException(Throwable cause) {
        super(cause);
    }
}
