package pl.app.common.shared.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("object not found");
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
