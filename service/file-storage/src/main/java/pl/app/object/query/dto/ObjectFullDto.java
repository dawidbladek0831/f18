package pl.app.object.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import pl.app.object.application.domain.RevisionType;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectFullDto implements Serializable {
    private ObjectId objectId;
    private String key;
    private ObjectId containerId;
    private Set<RevisionDto> revisions;
    private Map<String, Object> metadata;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RevisionDto implements Serializable {
        private Integer revisionId;
        private RevisionType revisionType;
        private Integer size;
        private String storageId;
    }
}