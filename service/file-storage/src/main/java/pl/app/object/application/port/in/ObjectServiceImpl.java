package pl.app.object.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.config.AuthorizationService;
import pl.app.config.SecurityConfig;
import pl.app.container.model.RevisionPolicyType;
import pl.app.container.service.ContainerQueryService;
import pl.app.object.application.domain.ObjectAggregate;
import pl.app.object.application.domain.ObjectEvent;
import pl.app.object.application.domain.ObjectException;
import pl.app.shared.EventPublisher;
import pl.app.storage.StorageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static pl.app.object.application.domain.ObjectAggregate.METADATA_OWNER_ID_KEY;

@Component
@RequiredArgsConstructor
class ObjectServiceImpl implements ObjectService {

    private static final Logger logger = LoggerFactory.getLogger(ObjectServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;
    private final StorageService storageService;

    private final ContainerQueryService containerQueryService;
    private final ObjectDomainRepository objectDomainRepository;

    private final RevisionOnPolicy revisionOnPolicy;
    private final RevisionOffPolicy revisionOffPolicy;

    private RevisionPolicy getPolicy(RevisionPolicyType type) {
        return switch (type) {
            case REVISION_ON -> revisionOnPolicy;
            case REVISION_OFF -> revisionOffPolicy;
        };
    }

    @Override
    public Mono<ObjectAggregate> crate(ObjectCommand.CreateObjectCommand command) {
        return Mono.fromCallable(() ->
                containerQueryService.fetchByName(command.getContainerName())
                        .flatMap(container -> {
                            RevisionPolicy revisionPolicy = getPolicy(container.getRevisionPolicyType());
                            return verifyKeyIsNotUsed(container.getContainerId(), command.getKey())
                                    .then(revisionPolicy.createObject(container, command));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("crating object: {}", command.getKey())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created object: {}", domain.getKey())
        ).doOnError(e ->
                logger.error("exception occurred while crating object: {}, exception: {}", command.getKey(), e.toString())
        );
    }

    public Mono<Void> verifyKeyIsNotUsed(ObjectId containerId, String key) {
        return mongoTemplate.exists(Query.query(
                        Criteria.where("key").is(key).and("containerId").is(containerId)
                ), ObjectAggregate.class)
                .flatMap(exist -> exist ? Mono.error(ObjectException.DuplicatedObjectKeyException.key(key)) : Mono.empty());
    }

    @Override
    public Mono<ObjectAggregate> update(ObjectCommand.UpdateObjectCommand command) {
        return Mono.fromCallable(() ->
                containerQueryService.fetchByName(command.getContainerName())
                        .flatMap(container -> {
                            RevisionPolicy revisionPolicy = getPolicy(container.getRevisionPolicyType());
                            return objectDomainRepository.fetchByContainerIdAndObjectKey(container.getContainerId(), command.getKey())
                                    .flatMap(domain -> AuthorizationService.verifySubjectHasAuthorityToEditObjectInContainer(container,  (String) domain.getMetadata().get(METADATA_OWNER_ID_KEY)).thenReturn(domain))
                                    .flatMap(domain -> {
                                        domain.updateMetaData(command.getMetadata());
                                        return revisionPolicy.updateObject(container, domain, command);
                                    });
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("updating object: {}", command.getKey())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("updated object: {}", domain.getKey())
        ).doOnError(e ->
                logger.error("exception occurred while updating object: {}, exception: {}", command.getKey(), e.toString())
        );
    }


    @Override
    public Mono<ObjectAggregate> remove(ObjectCommand.RemoveObjectCommand command) {
        return Mono.fromCallable(() ->
                containerQueryService.fetchByName(command.getContainerName())
                        .flatMap(container -> {
                            RevisionPolicy revisionPolicy = getPolicy(container.getRevisionPolicyType());
                            return objectDomainRepository.fetchByContainerIdAndObjectKey(container.getContainerId(), command.getKey())
                                    .flatMap(domain -> AuthorizationService.verifySubjectHasAuthorityToEditObjectInContainer(container, (String) domain.getMetadata().get(METADATA_OWNER_ID_KEY)).thenReturn(domain))
                                    .flatMap(domain -> revisionPolicy.removeObject(container, domain, command));
                        })
        ).doOnSubscribe(subscription ->
                logger.debug("removing object: {}", command.getKey())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("removed object: {}", domain.getKey())
        ).doOnError(e ->
                logger.error("exception occurred while removing object: {}, exception: {}", command.getKey(), e.toString())
        );
    }

    @Override
    public Mono<ObjectAggregate> deleteRevision(ObjectCommand.DeleteObjectRevisionCommand command) {
        return Mono.fromCallable(() ->
                containerQueryService.fetchByName(command.getContainerName())
                        .flatMap(container ->
                                objectDomainRepository.fetchByContainerIdAndObjectKey(container.getContainerId(), command.getKey())
                                        .flatMap(domain -> AuthorizationService.verifySubjectHasAuthorityToEditObjectInContainer(container, (String) domain.getMetadata().get(METADATA_OWNER_ID_KEY)).thenReturn(domain))
                                        .flatMap(domain -> {
                                            var deletedRevisions = domain.deleteRevisions(command.getRevisionIds());
                                            return mongoTemplate.save(domain)
                                                    .then(Flux.fromIterable(deletedRevisions)
                                                            .flatMap(deletedRevision ->
                                                                    storageService.delete(container.getContainerId(), deletedRevision.getStorageId())
                                                                            .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionDeleted(domain.getObjectId(), deletedRevision.getRevisionId())))
                                                            ).then()
                                                    )
                                                    .thenReturn(domain);
                                        })
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("deleting object revision: {}", command.getKey())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("deleted object revision: {}", domain.getKey())
        ).doOnError(e ->
                logger.error("exception occurred while deleting object revision: {}, exception: {}", command.getKey(), e.toString())
        );
    }

    @Override
    public Mono<ObjectAggregate> restoreRevision(ObjectCommand.RestoreObjectRevisionCommand command) {
        return Mono.fromCallable(() ->
                containerQueryService.fetchByName(command.getContainerName())
                        .flatMap(container ->
                                objectDomainRepository.fetchByContainerIdAndObjectKey(container.getContainerId(), command.getKey())
                                        .flatMap(domain -> AuthorizationService.verifySubjectHasAuthorityToEditObjectInContainer(container, (String) domain.getMetadata().get(METADATA_OWNER_ID_KEY)).thenReturn(domain))
                                        .flatMap(domain -> {
                                            var restoringRevision = domain.getRevisionByIdOrThrow(command.getRevisionId());
                                            var restoredRevision = domain.restoreRevision(command.getRevisionId());
                                            return mongoTemplate.save(domain)
                                                    .then(storageService.copy(container.getContainerId(), restoringRevision.getStorageId(), restoredRevision.getStorageId()))
                                                    .then(eventPublisher.publish(new ObjectEvent.ObjectRevisionRestored(domain.getObjectId(), restoredRevision.getRevisionId())))
                                                    .thenReturn(domain);
                                        })
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("restoring object revision: {}", command.getKey())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("restored object revision: {}", domain.getKey())
        ).doOnError(e ->
                logger.error("exception occurred while restoring object revision: {}, exception: {}", command.getKey(), e.toString())
        );
    }
}
