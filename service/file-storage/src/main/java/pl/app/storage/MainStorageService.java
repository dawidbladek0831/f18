package pl.app.storage;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.app.shared.EventPublisher;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Primary
@Component
class MainStorageService implements StorageService {
    private static final Logger logger = LoggerFactory.getLogger(MainStorageService.class);
    private EventPublisher eventPublisher;

    private StorageService activeService;


    public MainStorageService(EventPublisher eventPublisher,
                              LocalStorageService localStorageService) {
        this.eventPublisher = eventPublisher;
        this.activeService = localStorageService;
    }

    @Override
    public Mono<Void> init(ObjectId containerId) {
        return Mono.fromCallable(() ->
                activeService.init(containerId)
                        .then(eventPublisher.publish(new FileEvent.StorageInitialized(containerId)))
        ).doOnSubscribe(subscription ->
                logger.debug("initialization of storage: {}", containerId)
        ).flatMap(Function.identity()).doOnSuccess(onSuccess ->
                logger.debug("initialized of storage: {}", containerId)
        ).doOnError(e ->
                logger.error("exception occurred while initialization of storage: {}, exception: {}", containerId, e.toString())
        );
    }

    @Override
    public Mono<Void> create(ObjectId containerId, String storageId, byte[] content) {
        return Mono.fromCallable(() -> activeService.create(containerId, storageId, content)
                .then(eventPublisher.publish(new FileEvent.FileStored(containerId, storageId)))
        ).doOnSubscribe(subscription ->
                logger.debug("crating file: {}", storageId)
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("crated file: {}", storageId)
        ).doOnError(e ->
                logger.error("exception occurred while crating file: {}, exception: {}", storageId, e.toString())
        );
    }

    @Override
    public Mono<Void> copy(ObjectId containerId, String storageId, String newStorageId) {
        return Mono.fromCallable(() -> activeService.copy(containerId, storageId, newStorageId)
                .then(eventPublisher.publish(new FileEvent.FileCopied(containerId, storageId, newStorageId)))

        ).doOnSubscribe(subscription ->
                logger.debug("coping file: {}", storageId)
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("copied file: {}", storageId)
        ).doOnError(e ->
                logger.error("exception occurred while coping file: {}, exception: {}", storageId, e.toString())
        );
    }

    @Override
    public Mono<Void> delete(ObjectId containerId, String storageId) {
        return Mono.fromCallable(() -> activeService.delete(containerId, storageId)
                .then(eventPublisher.publish(new FileEvent.FileDeleted(containerId, storageId)))
        ).doOnSubscribe(subscription ->
                logger.debug("deleting file: {}", storageId)
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("deleted file: {}", storageId)
        ).doOnError(e ->
                logger.error("exception occurred while deleting file: {}, exception: {}", storageId, e.toString())
        );
    }

    @Override
    public Mono<byte[]> get(ObjectId containerId, String storageId) {
        return activeService.get(containerId, storageId);
    }
}
