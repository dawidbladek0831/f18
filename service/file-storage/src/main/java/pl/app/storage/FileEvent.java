package pl.app.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

public interface FileEvent {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class StorageInitialized implements Serializable {
        private ObjectId containerId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FileStored implements Serializable {
        private ObjectId containerId;
        private String storageId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FileDeleted implements Serializable {
        private ObjectId containerId;
        private String storageId;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class FileCopied implements Serializable {
        private ObjectId containerId;
        private String storageId;
        private String newStorageId;
    }
}
