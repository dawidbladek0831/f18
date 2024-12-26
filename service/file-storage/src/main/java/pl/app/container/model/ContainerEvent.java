package pl.app.container.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface ContainerEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ContainerCreated implements Serializable {
        private ObjectId containerId;
        private String name;
        private Container.ContainerType type;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ContainerUpdated implements Serializable {
        private ObjectId containerId;
        private String name;
        private Container.ContainerType type;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ContainerDeleted implements Serializable {
        private ObjectId containerId;
        private String name;
        private Container.ContainerType type;
    }
}
