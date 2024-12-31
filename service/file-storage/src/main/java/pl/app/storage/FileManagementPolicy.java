package pl.app.storage;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.object.application.domain.ObjectEvent;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class FileManagementPolicy {
    private final Logger logger = LoggerFactory.getLogger(FileManagementPolicy.class);
    private final StorageService storageService;

    @KafkaListener(
            id = "object-revision-deleted-event-listener--storage",
            groupId = "${app.kafka.consumer.group-id}--storage",
            topics = "${app.kafka.topic.object-revision-deleted.name}"
    )
    public void objectRevisionDeleted(ConsumerRecord<ObjectId, ObjectEvent.ObjectRevisionDeleted> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        storageService.delete(event.getContainerId(), event.getStorageId()).subscribe();
    }

    @KafkaListener(
            id = "object-revision-restored-event-listener--storage",
            groupId = "${app.kafka.consumer.group-id}--storage",
            topics = "${app.kafka.topic.object-revision-restored.name}"
    )
    public void objectRevisionRestored(ConsumerRecord<ObjectId, ObjectEvent.ObjectRevisionRestored> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        storageService.copy(event.getContainerId(), event.getStorageId(), event.getLeadStorageId()).subscribe();
    }
}
