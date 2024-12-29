package pl.app.object.application.port.in;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import pl.app.container.service.dto.ContainerDto;
import pl.app.object.application.domain.ObjectAggregate;
import pl.app.object.application.domain.ObjectEvent;
import pl.app.object.application.domain.ObjectException;
import pl.app.object.application.domain.RevisionType;
import pl.app.shared.EventPublisher;
import pl.app.storage.StorageService;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class RevisionOnPolicy implements RevisionPolicy {
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
        var revision = objectAggregate.addRevision(RevisionType.UPDATED, command.getContent());
        var event = new ObjectEvent.ObjectRevisionCreated(objectAggregate.getObjectId(), revision.getRevisionId());
        return mongoTemplate.save(objectAggregate)
                .then(storageService.create(container.getContainerId(), revision.getStorageId(), command.getContent()))
                .then(eventPublisher.publish(new ObjectEvent.ObjectUpdated(objectAggregate.getObjectId())))
                .then(eventPublisher.publish(event))
                .thenReturn(objectAggregate);
    }

    @Override
    public Mono<ObjectAggregate> removeObject(ContainerDto container, ObjectAggregate objectAggregate, ObjectCommand.RemoveObjectCommand command) {
        Optional<ObjectAggregate.Revision> leadRevision = objectAggregate.getLeadRevision();
        if (leadRevision.isPresent() && RevisionType.DELETED.equals(leadRevision.get().getRevisionType())) {
            return Mono.error(ObjectException.ObjectAlreadyDeletedException::new);
        }
        var revision = objectAggregate.addRevision(RevisionType.DELETED, new byte[0]);
        return mongoTemplate.save(objectAggregate)
                .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionCreated(objectAggregate.getObjectId(), revision.getRevisionId())))
                .then(eventPublisher.publish(new ObjectEvent.ObjectRemoved(objectAggregate.getObjectId())))
                .thenReturn(objectAggregate);
    }
}
