package pl.app.common.shared.exception;

public class IllegalArgumentException extends RuntimeException {
    public IllegalArgumentException() {
        super("method has been invoked with illegal or inappropriate argument");
    }

    public IllegalArgumentException(String message) {
        super(message);
    }

    public IllegalArgumentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
