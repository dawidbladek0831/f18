package pl.app.object.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.app.object.application.domain.RevisionType;

import java.util.*;


@Document(collection = "object")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ObjectAggregateQuery {
    @Id
    private ObjectId objectId;
    private String key;
    private ObjectId containerId;
    private Set<Revision> revisions;
    private Map<String, Object> metadata;

    public Optional<Revision> getLeadRevision() {
        return revisions.stream().max(Comparator.comparing(Revision::getRevisionId));
    }

    public Optional<Revision> getRevisionById(Integer revisionId) {
        return revisions.stream()
                .filter(revision -> Objects.equals(revision.getRevisionId(), revisionId))
                .findAny();
    }

    @Getter
    @NoArgsConstructor
    public static class Revision {
        private Integer revisionId;
        private RevisionType revisionType;
        private Integer size;
        private String storageId;
    }
}
