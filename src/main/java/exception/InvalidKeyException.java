package exception;

public class InvalidKeyException extends RuntimeException{
    public InvalidKeyException() {}

    public InvalidKeyException(String msg) {
        super(msg);
    }

    public InvalidKeyException(Throwable throwable) {
        super(throwable);
    }

    public InvalidKeyException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
