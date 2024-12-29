package pl.app.common.shared.exception;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException() {
        super("object is in a state that makes it impossible to perform the operation");
    }

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
