package pl.app.common.shared.exception;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super("Invalid object");
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
