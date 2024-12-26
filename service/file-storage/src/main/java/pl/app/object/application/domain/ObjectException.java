package pl.app.object.application.domain;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface ObjectException {
    class NotFoundObjectException extends NotFoundException {
        public NotFoundObjectException() {
            super("not found object");
        }

        public NotFoundObjectException(String message) {
            super(message);
        }

        public static NotFoundObjectException key(String key) {
            return new NotFoundObjectException("not found object: " + key);
        }
    }
    class NotFoundObjectRevisionException extends NotFoundException {
        public NotFoundObjectRevisionException() {
            super("not found object revision");
        }

        public NotFoundObjectRevisionException(String message) {
            super(message);
        }

        public static NotFoundObjectRevisionException revisionId(Integer revisionId) {
            return new NotFoundObjectRevisionException("not found object revision: " + revisionId);
        }
    }
    class ObjectAlreadyDeletedException extends ValidationException {
        public ObjectAlreadyDeletedException() {
            super("the object has already been marked as deleted");
        }

        public ObjectAlreadyDeletedException(String message) {
            super(message);
        }
    }

    class DuplicatedObjectKeyException extends ValidationException {
        public DuplicatedObjectKeyException() {
            super("there is object for given key");
        }

        public DuplicatedObjectKeyException(String message) {
            super(message);
        }

        public static DuplicatedObjectKeyException key(String key) {
            return new DuplicatedObjectKeyException(MessageFormat.format("there is object for given key: {0}", key));
        }
    }
}
