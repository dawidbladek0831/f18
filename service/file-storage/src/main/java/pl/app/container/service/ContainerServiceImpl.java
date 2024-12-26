package pl.app.container.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.container.model.Container;
import pl.app.container.model.ContainerEvent;
import pl.app.container.model.ContainerException;
import pl.app.container.service.dto.ContainerDto;
import pl.app.shared.EventPublisher;
import pl.app.storage.StorageService;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
class ContainerServiceImpl implements ContainerService {
    private static final Logger logger = LoggerFactory.getLogger(ContainerServiceImpl.class);

    private final ReactiveMongoTemplate mongoTemplate;
    private final EventPublisher eventPublisher;

    private final ContainerQueryService containerQueryService;
    private final ContainerMapper mapper;
    private final StorageService storageService;

    @Override
    public Mono<ContainerDto> create(ContainerDto dto) {
        return Mono.fromCallable(() -> {
                    return verifyNameIsNotUsed(dto.getName())
                            .then(Mono.defer(() -> {
                                var domain = new Container(dto.getName(), dto.getType(), dto.getRevisionPolicyType());
                                return storageService.init(domain.getContainerId())
                                        .then(mongoTemplate.insert(domain))
                                        .then(eventPublisher.publish(new ContainerEvent.ContainerCreated(domain.getContainerId(), domain.getName(), domain.getType())))
                                        .thenReturn(domain);
                            }))
                            .map(domain -> mapper.map(domain, ContainerDto.class));
                }
        ).doOnSubscribe(subscription ->
                logger.debug("crating container: {}", dto.getName())
        ).flatMap(Function.identity()).doOnSuccess(domain ->
                logger.debug("created container: {}", domain.getContainerId())
        ).doOnError(e ->
                logger.error("exception occurred while crating container: {}, exception: {}", dto.getName(), e.toString())
        );
    }

    @Override
    public Mono<ContainerDto> update(String name, ContainerDto dto) {
        return Mono.error(() -> new RuntimeException("not implemented yet"));
    }

    @Override
    public Mono<Void> deleteByName(String name) {
        return Mono.error(() -> new RuntimeException("not implemented yet"));
    }

    public Mono<Void> verifyNameIsNotUsed(String name) {
        return mongoTemplate.exists(Query.query(Criteria.where("name").is(name)), Container.class)
                .flatMap(exist -> exist ? Mono.error(ContainerException.DuplicatedContainerNameException.name(name)) : Mono.empty());
    }
}
