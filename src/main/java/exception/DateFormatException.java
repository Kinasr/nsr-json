package exception;

import com.sun.net.httpserver.Headers;
import lombok.experimental.Helper;

public class DateFormatException extends RuntimeException{
    public DateFormatException() {
    }

    public DateFormatException(String message) {
        super(message);
    }

    public DateFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateFormatException(Throwable cause) {
        super(cause);
    }
}
