package pl.app.container.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.container.model.Container;
import pl.app.container.model.ContainerException;
import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
class ContainerQueryServiceImpl implements ContainerQueryService {
    private final ContainerMapper mapper;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<ContainerDto> fetchAll() {
        return mongoTemplate.query(Container.class).all()
                .map(e -> mapper.map(e, ContainerDto.class));
    }

    @Override
    public Mono<ContainerDto> fetchByName(String name) {
        return mongoTemplate.query(Container.class)
                .matching(Query.query(Criteria.where("name").is(name)))
                .one()
                .map(e -> mapper.map(e, ContainerDto.class))
                .switchIfEmpty(Mono.error(() -> ContainerException.NotFoundContainerException.name(name)));
    }
}
