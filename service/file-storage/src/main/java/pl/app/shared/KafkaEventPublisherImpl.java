package pl.app.shared;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.app.config.KafkaTopicConfigurationProperties;
import pl.app.container.model.ContainerEvent;
import pl.app.object.application.domain.ObjectEvent;
import pl.app.storage.FileEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
@RequiredArgsConstructor
class KafkaEventPublisherImpl implements EventPublisher {
    private final Logger logger = LoggerFactory.getLogger(KafkaEventPublisherImpl.class);
    private final KafkaTemplate<ObjectId, Object> kafkaTemplate;
    private final KafkaTopicConfigurationProperties topicNames;

    @Override
    public Mono<Void> publish(Object event) {
        return switch (event) {
            case ContainerEvent.ContainerCreated e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getContainerCreated().getName(), e.getContainerId(), event)).then();
            case ContainerEvent.ContainerUpdated e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getContainerUpdated().getName(), e.getContainerId(), event)).then();
            case ContainerEvent.ContainerDeleted e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getContainerDeleted().getName(), e.getContainerId(), event)).then();

            case ObjectEvent.ObjectCreated e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectCreated().getName(), e.getObjectId(), event)).then();
            case ObjectEvent.ObjectUpdated e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectUpdated().getName(), e.getObjectId(), event)).then();
            case ObjectEvent.ObjectRemoved e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectRemoved().getName(), e.getObjectId(), event)).then();
            case ObjectEvent.ObjectDeleted e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectDeleted().getName(), e.getObjectId(), event)).then();
            case ObjectEvent.ObjectRevisionCreated e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectRevisionCreated().getName(), e.getObjectId(), event)).then();
            case ObjectEvent.ObjectRevisionDeleted e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectRevisionDeleted().getName(), e.getObjectId(), event)).then();
            case ObjectEvent.ObjectRevisionRestored e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getObjectRevisionRestored().getName(), e.getObjectId(), event)).then();

            case FileEvent.StorageInitialized e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getStorageInitialized().getName(), e.getContainerId(), event)).then();
            case FileEvent.StorageCleaned e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getStorageCleaned().getName(), e.getContainerId(), event)).then();
            case FileEvent.FileStored e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getFileStored().getName(), e.getContainerId(), event)).then();
            case FileEvent.FileDeleted e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getFileDeleted().getName(), e.getContainerId(), event)).then();
            case FileEvent.FileCopied e ->
                    Mono.fromFuture(kafkaTemplate.send(topicNames.getFileCopied().getName(), e.getContainerId(), event)).then();
            default -> {
                logger.error("event {} is not configured in EventPublisher", event.getClass().getSimpleName());
                yield Mono.empty();
            }
        };
    }

    @Override
    public Mono<Void> publish(Collection<Object> events) {
        return Flux.fromIterable(events)
                .flatMap(this::publish)
                .then();
    }
}
