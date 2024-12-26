package pl.app.container.model;

import pl.app.common.shared.exception.NotFoundException;
import pl.app.common.shared.exception.ValidationException;

import java.text.MessageFormat;

public interface ContainerException {
    class NotFoundContainerException extends NotFoundException {
        public NotFoundContainerException() {
            super("not found container");
        }

        public NotFoundContainerException(String message) {
            super(message);
        }

        public static NotFoundContainerException name(String name) {
            return new NotFoundContainerException("not found container: " + name);
        }
    }
    class DuplicatedContainerNameException extends ValidationException {
        public DuplicatedContainerNameException() {
            super("there are container for given name");
        }

        public DuplicatedContainerNameException(String message) {
            super(message);
        }

        public static DuplicatedContainerNameException name(String name) {
            return new DuplicatedContainerNameException(MessageFormat.format("there are container for given name: {0}", name));
        }
    }
}
