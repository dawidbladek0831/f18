package pl.app.common.shared.config;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import pl.app.common.shared.exception.*;

//@Configuration
public class ExceptionAdviceConfig {
    @Bean
    ExceptionAdvice ExceptionAdvice() {
        return new ExceptionAdvice();
    }

    @RestControllerAdvice
    public static class ExceptionAdvice {
        private static final Logger logger = LoggerFactory.getLogger(ExceptionAdviceConfig.class);

        @ExceptionHandler(AuthenticationException.class)
        public ProblemDetail authenticationExceptionHandler(AuthenticationException exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle(new AuthenticationException().getMessage());
            return problemDetails;
        }

        @ExceptionHandler(AuthorizationException.class)
        public ProblemDetail authorizationExceptionHandler(AuthorizationException exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.FORBIDDEN,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle(new AuthorizationException().getMessage());
            return problemDetails;
        }

        @ExceptionHandler(InvalidStateException.class)
        public ProblemDetail invalidStateExceptionHandler(InvalidStateException exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle(new InvalidStateException().getMessage());
            return problemDetails;
        }

        @ExceptionHandler(IOException.class)
        public ProblemDetail iOExceptionHandler(IOException exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle(new IOException().getMessage());
            return problemDetails;
        }

        @ExceptionHandler(NotFoundException.class)
        public ProblemDetail notFoundExceptionHandler(NotFoundException exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle(new NotFoundException().getMessage());
            return problemDetails;
        }

        @ExceptionHandler({ValidationException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
        public ProblemDetail validationExceptionHandler(Exception exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle(new ValidationException().getMessage());
            return problemDetails;
        }

        @ExceptionHandler(Exception.class)
        public ProblemDetail exceptionHandler(Exception exception, ServerWebExchange exchange) {
            logger.error(exception.getMessage(), exception);
            ProblemDetail problemDetails = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    exception.getMessage()
            );
            problemDetails.setType(exchange.getRequest().getURI());
            problemDetails.setTitle("An exception has occurred");
            return problemDetails;
        }
    }

}
