package pl.app.object.query;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.app.container.service.ContainerQueryService;
import pl.app.object.application.domain.ObjectException;
import pl.app.object.application.domain.RevisionType;
import pl.app.storage.StorageService;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class FileQueryServiceImpl implements FileQueryService {
    private final StorageService storageService;
    private final ObjectQueryService objectQueryService;
    private final ContainerQueryService containerQueryService;

    @Override
    public Mono<Resource> fetchByContainerAndKeyAsResource(String containerName, String key) {
        return objectQueryService.fetchOne(containerName, key, ObjectAggregateQuery.class)
                .flatMap(object -> {
                    var revisionOpt = object.getLeadRevision();
                    if (revisionOpt.isEmpty()) {
                        return Mono.error(ObjectException.NotFoundObjectRevisionException::new);
                    }
                    if (RevisionType.DELETED.equals(revisionOpt.get().getRevisionType())) {
                        return Mono.error(() -> new ObjectException.NotFoundObjectRevisionException("lead revision has been marked as deleted"));
                    }
                    return storageService.get(object.getContainerId(), revisionOpt.get().getStorageId())
                            .map(bytes -> new CustomByteArrayResource(bytes, object.getKey()));
                });
    }

    @Override
    public Mono<Resource> fetchByContainerAndKeyAndRevisionAsResource(String containerName, String key, Integer revisionId) {
        return objectQueryService.fetchOne(containerName, key, ObjectAggregateQuery.class)
                .flatMap(object -> {
                    var revisionOpt = object.getRevisionById(revisionId);
                    if (revisionOpt.isEmpty()) {
                        return Mono.error(() -> ObjectException.NotFoundObjectRevisionException.revisionId(revisionId));
                    }
                    if (RevisionType.DELETED.equals(revisionOpt.get().getRevisionType())) {
                        return Mono.error(() -> new ObjectException.NotFoundObjectRevisionException("revision has been marked as deleted"));
                    }
                    return storageService.get(object.getContainerId(), revisionOpt.get().getStorageId())
                            .map(bytes -> new CustomByteArrayResource(bytes, object.getKey()));
                });
    }

    static class CustomByteArrayResource extends ByteArrayResource {
        private String filename;

        public CustomByteArrayResource(byte[] byteArray, String filename) {
            super(byteArray);
            setFilename(filename);
        }

        @Override
        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }
}
