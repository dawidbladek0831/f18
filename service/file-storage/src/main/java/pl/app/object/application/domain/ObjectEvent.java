package pl.app.object.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface ObjectEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectCreated implements Serializable {
        private ObjectId objectId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectUpdated implements Serializable {
        private ObjectId objectId;
        private ObjectId containerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectRemoved implements Serializable {
        private ObjectId objectId;
        private ObjectId containerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectDeleted implements Serializable {
        private ObjectId objectId;
        private ObjectId containerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectRevisionCreated implements Serializable {
        private ObjectId objectId;
        private ObjectId containerId;
        private Integer revisionId;
        private String storageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectRevisionDeleted implements Serializable {
        private ObjectId objectId;
        private ObjectId containerId;
        private Integer revisionId;
        private String storageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ObjectRevisionRestored implements Serializable {
        private ObjectId objectId;
        private ObjectId containerId;
        private Integer revisionId;
        private String storageId;
        private Integer leadRevisionId;
        private String leadStorageId;
    }
}
