package pl.app.object.application.port.in;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.container.service.dto.ContainerDto;
import pl.app.object.application.domain.ObjectAggregate;
import pl.app.object.application.domain.ObjectEvent;
import pl.app.object.application.domain.RevisionType;
import pl.app.shared.EventPublisher;
import pl.app.storage.StorageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
class RevisionOffPolicy implements RevisionPolicy {
    private final EventPublisher eventPublisher;
    private final StorageService storageService;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<ObjectAggregate> createObject(ContainerDto container, ObjectCommand.CreateObjectCommand command) {
        var objectAggregate = new ObjectAggregate(container.getContainerId(), command.getKey(), command.getMetadata());
        var revision = objectAggregate.addRevision(RevisionType.CREATED, command.getContent());
        return mongoTemplate.insert(objectAggregate)
                .then(storageService.create(container.getContainerId(), revision.getStorageId(), command.getContent()))
                .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionCreated(objectAggregate.getObjectId(), revision.getRevisionId())))
                .then(eventPublisher.publish(new ObjectEvent.ObjectCreated(objectAggregate.getObjectId())))
                .thenReturn(objectAggregate);
    }

    @Override
    public Mono<ObjectAggregate> updateObject(ContainerDto container, ObjectAggregate objectAggregate, ObjectCommand.UpdateObjectCommand command) {
        Set<ObjectAggregate.Revision> deletedRevisions = objectAggregate.deleteAllRevisions();
        var newRevision = objectAggregate.addRevision(RevisionType.UPDATED, command.getContent());
        return mongoTemplate.save(objectAggregate)
                .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionCreated(objectAggregate.getObjectId(), newRevision.getRevisionId())))
                .then(Flux.fromIterable(deletedRevisions)
                        .flatMap(deletedRevision ->
                                storageService.delete(container.getContainerId(), deletedRevision.getStorageId())
                                        .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionDeleted(objectAggregate.getObjectId(), deletedRevision.getRevisionId())))
                        ).then())
                .then(storageService.create(container.getContainerId(), newRevision.getStorageId(), command.getContent()))
                .then(eventPublisher.publish(new ObjectEvent.ObjectUpdated(objectAggregate.getObjectId())))
                .thenReturn(objectAggregate);
    }

    @Override
    public Mono<ObjectAggregate> removeObject(ContainerDto container, ObjectAggregate objectAggregate, ObjectCommand.RemoveObjectCommand command) {
        Set<ObjectAggregate.Revision> deletedRevisions = objectAggregate.deleteAllRevisions();
        return mongoTemplate.remove(objectAggregate)
                .then(Flux.fromIterable(deletedRevisions)
                        .flatMap(deletedRevision ->
                                storageService.delete(container.getContainerId(), deletedRevision.getStorageId())
                                        .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionDeleted(objectAggregate.getObjectId(), deletedRevision.getRevisionId())))
                        ).then())
                .then(eventPublisher.publish(new ObjectEvent.ObjectDeleted(objectAggregate.getObjectId())))
                .thenReturn(objectAggregate);
    }
}
