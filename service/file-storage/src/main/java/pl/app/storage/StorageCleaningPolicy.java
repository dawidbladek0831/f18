package pl.app.storage;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.app.container.model.ContainerEvent;

@ConditionalOnProperty(value = "app.kafka.listeners.enable", matchIfMissing = true)
@Component
@RequiredArgsConstructor
class StorageCleaningPolicy {
    private final Logger logger = LoggerFactory.getLogger(StorageCleaningPolicy.class);
    private final StorageService storageService;

    @KafkaListener(
            id = "container-deleted-event-listener--storage",
            groupId = "${app.kafka.consumer.group-id}--storage",
            topics = "${app.kafka.topic.container-deleted.name}"
    )
    public void containerDeleted(ConsumerRecord<ObjectId, ContainerEvent.ContainerDeleted> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        final var event = record.value();
        storageService.cleanUp(event.getContainerId()).subscribe();
    }
}
