package pl.app.container.service;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.app.container.model.ContainerEvent;
import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

@Primary
@Service
class ContainerQueryServiceCache implements ContainerQueryService {
    private final Logger logger = LoggerFactory.getLogger(ContainerQueryServiceCache.class);
    private final ContainerQueryServiceImpl service;
    private final AsyncCache<String, ContainerDto> cache; // container name -> container

    public ContainerQueryServiceCache(ContainerQueryServiceImpl service) {
        this.service = service;
        this.cache = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .buildAsync();
    }

    @Override
    public Flux<ContainerDto> fetchAll() {
        return Flux.fromIterable(cache.asMap().values())
                .flatMap(Mono::fromFuture)
                .switchIfEmpty(service.fetchAll());
    }

    @Override
    public Mono<ContainerDto> fetchByName(String containerName) {
        return Mono.fromFuture(cache.get(containerName, new BiFunction<String, Executor, CompletableFuture<? extends ContainerDto>>() {
            @Override
            public CompletableFuture<? extends ContainerDto> apply(String s, Executor executor) {
                return service.fetchByName(containerName).toFuture();
            }
        }));
    }

    public Mono<Void> refreshCache() {
        return Mono.fromCallable(() ->
                Mono.fromRunnable(() -> cache.synchronous().invalidateAll())
                        .then(service.fetchAll().flatMap(containerDto -> Mono.fromFuture(cache.get(containerDto.getName(),
                                        (key, executor) -> CompletableFuture.completedFuture(containerDto))))
                                .then()
                        )
        ).doOnSubscribe(subscription ->
                logger.debug("refreshing cache")
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("refreshed cache")
        ).doOnError(e ->
                logger.error("exception occurred while refreshing cache, exception: {}", e.toString())
        );
    }

    @KafkaListener(
            id = "container-created--event-listener--container",
            groupId = "${app.kafka.consumer.group-id}--container",
            topics = "${app.kafka.topic.container-created.name}"
    )
    public void containerCreated(ConsumerRecord<ObjectId, ContainerEvent.ContainerCreated> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        refreshCache().subscribe();
    }

    @KafkaListener(
            id = "container-updated--event-listener--container",
            groupId = "${app.kafka.consumer.group-id}--container",
            topics = "${app.kafka.topic.container-updated.name}"
    )
    public void containerUpdated(ConsumerRecord<ObjectId, ContainerEvent.ContainerUpdated> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        refreshCache().subscribe();
    }

    @KafkaListener(
            id = "container-deleted--event-listener--container",
            groupId = "${app.kafka.consumer.group-id}--container",
            topics = "${app.kafka.topic.container-deleted.name}"
    )
    public void containerDeleted(ConsumerRecord<ObjectId, ContainerEvent.ContainerDeleted> record) {
        logger.debug("received event {} {}-{} key: {},value: {}", record.value().getClass().getSimpleName(), record.partition(), record.offset(), record.key(), record.value());
        refreshCache().subscribe();
    }
}
