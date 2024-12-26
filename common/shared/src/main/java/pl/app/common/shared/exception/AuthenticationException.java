package pl.app.common.shared.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("you are not authenticated");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
