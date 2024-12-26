package pl.app.object.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface ObjectCommand {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class CreateObjectCommand implements Serializable {
        private String key;
        private String containerName;
        private byte[] content;
        private Map<String, Object> metadata;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateObjectCommand implements Serializable {
        private String key;
        private String containerName;
        private byte[] content;
        private Map<String, Object> metadata;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RemoveObjectCommand implements Serializable {
        private String key;
        private String containerName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeleteObjectRevisionCommand implements Serializable {
        private String key;
        private String containerName;
        private Set<Integer> revisionIds;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RestoreObjectRevisionCommand implements Serializable {
        private String key;
        private String containerName;
        private Integer revisionId;
    }
}