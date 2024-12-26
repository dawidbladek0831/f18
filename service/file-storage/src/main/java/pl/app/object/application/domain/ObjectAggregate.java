package pl.app.object.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.stream.Collectors;

@Document(collection = "object")
@Getter
@NoArgsConstructor
public class ObjectAggregate {
    @Id
    private ObjectId objectId;
    private String key;
    private ObjectId containerId;

    private Set<Revision> revisions;
    public static final String METADATA_EXTENSION_KEY = "extension";
    public static final String METADATA_OWNER_ID_KEY = "ownerId";
    private Map<String, Object> metadata;

    public ObjectAggregate(ObjectId containerId, String key, Map<String, Object> metadata) {
        this.objectId = ObjectId.get();
        this.containerId = containerId;
        this.key = key;
        this.metadata = Objects.isNull(metadata) ? new HashMap<>() : new HashMap<>(metadata);
        this.metadata.put(METADATA_EXTENSION_KEY, getFileExtension(key).orElse(""));
        this.revisions = new LinkedHashSet<>();
    }

    public Revision addRevision(RevisionType revisionType, byte[] content) {
        return switch (revisionType) {
            case CREATED, UPDATED, RESTORED -> {
                final Integer newRevisionId = getNextRevisionId();
                final String storageId = getStorageId(newRevisionId, (String) metadata.get(METADATA_EXTENSION_KEY));
                Revision newRevision = new Revision(newRevisionId, revisionType, storageId, content.length);
                revisions.add(newRevision);
                yield newRevision;
            }
            case DELETED -> {
                final Integer newRevisionId = getNextRevisionId();
                Revision newRevision = new Revision(newRevisionId, revisionType, null, 0);
                revisions.add(newRevision);
                yield newRevision;
            }
        };
    }

    public Set<Revision> deleteAllRevisions() {
        Set<Revision> revisionToDelete = new LinkedHashSet<>(revisions);
        revisions.clear();
        return revisionToDelete;
    }

    public Revision deleteRevision(Integer revisionId) {
        Revision revision = getRevisionByIdOrThrow(revisionId);
        revisions.remove(revision);
        return revision;
    }
    public Set<Revision> deleteRevisions(Set<Integer> revisionIds) {
        return revisionIds.stream().map(this::deleteRevision)
                .collect(Collectors.toSet());
    }
    public Revision restoreRevision(Integer revisionId) {
        Revision revision = getRevisionByIdOrThrow(revisionId);
        final Integer newRevisionId = getNextRevisionId();
        final String newRevisionStorageId = getStorageId(newRevisionId, (String) metadata.get(METADATA_EXTENSION_KEY));
        Revision newRevision = new Revision(newRevisionId, RevisionType.RESTORED, newRevisionStorageId, revision.getSize());
        revisions.add(newRevision);
        return newRevision;
    }

    public void updateMetaData(Map<String, Object> metadata) {
        if (Objects.isNull(metadata)) {
            return;
        }
        this.metadata.putAll(metadata);
    }

    public Optional<Revision> getLeadRevision() {
        return revisions.stream().max(Comparator.comparing(Revision::getRevisionId));
    }

    public Optional<Revision> getRevisionById(Integer revisionId) {
        return revisions.stream()
                .filter(revision -> Objects.equals(revision.getRevisionId(), revisionId))
                .findAny();
    }

    public Revision getRevisionByIdOrThrow(Integer revisionId) {
        return getRevisionById(revisionId)
                .orElseThrow(() -> ObjectException.NotFoundObjectRevisionException.revisionId(revisionId));
    }

    private Integer getNextRevisionId() {
        final Integer currentRevisionId = getLeadRevision().map(Revision::getRevisionId).orElse(0);
        return currentRevisionId + 1;
    }

    private String getStorageId(Integer revisionId, String extension) {
        return objectId + "_" + revisionId + "." + extension;
    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }


    @Getter
    @NoArgsConstructor
    public static class Revision {
        private Integer revisionId;
        private RevisionType revisionType;
        private Integer size;
        private String storageId;

        public Revision(Integer revisionId, RevisionType revisionType, String storageId, Integer size) {
            this.revisionId = revisionId;
            this.revisionType = revisionType;
            this.size = size;
            this.storageId = storageId;
        }
    }
}
