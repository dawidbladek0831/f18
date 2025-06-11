package pl.app.object.query;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.container.service.ContainerQueryService;
import pl.app.object.application.domain.ObjectException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ObjectQueryServiceImpl implements ObjectQueryService {
    private final ObjectMapper mapper;
    private final ReactiveMongoTemplate mongoTemplate;

    private final ContainerQueryService containerQueryService;

    @Override
    public <T> Flux<T> fetchAll(Class<T> dtoClass) {
        return mongoTemplate.query(ObjectAggregateQuery.class).all()
                .map(e -> mapper.map(e, dtoClass));
    }

    @Override
    public <T> Flux<T> fetchAll(String containerName, Map<String, Object> filters, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMapMany(container -> {
                    filters.put("containerId", container.getContainerId().toString());
                    return fetchFluxBy(filters);
                })
                .map(e -> mapper.map(e, dtoClass));
    }


    @Override
    public <T> Mono<T> fetchOne(String containerName, String key, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> {
                    HashMap<String, Object> filters = new HashMap<>();
                    filters.put("containerId", container.getContainerId());
                    filters.put("key", key);
                    return fetchMonoBy(filters);
                })
                .map(e -> mapper.map(e, dtoClass))
                .switchIfEmpty(Mono.error(() -> ObjectException.NotFoundObjectException.key(key)));
    }

    @Override
    public <T> Mono<T> fetchOne(String containerName, String key, Set<Integer> revision, Map<String, Object> filters, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> {
                    filters.put("containerId", container.getContainerId().toString());
                    filters.put("key", key);
                    return fetchMonoBy(filters);
                })
                .map(domain -> {
                    if (Objects.isNull(revision) || revision.isEmpty()) {
                        return domain;
                    }
                    Set<ObjectAggregateQuery.Revision> filteredRevisions = domain.getRevisions().stream()
                            .filter(e -> revision.contains(e.getRevisionId()))
                            .collect(Collectors.toSet());
                    domain.setRevisions(filteredRevisions);
                    return domain;
                })
                .map(e -> mapper.map(e, dtoClass))
                .switchIfEmpty(Mono.error(() -> ObjectException.NotFoundObjectException.key(key)));
    }


    private Mono<ObjectAggregateQuery> fetchMonoBy(Map<String, Object> filters) {
        return mongoTemplate.query(ObjectAggregateQuery.class)
                .matching(Query.query(createCriteria(filters)))
                .one();
    }

    private Flux<ObjectAggregateQuery> fetchFluxBy(Map<String, Object> filters) {
        return mongoTemplate.query(ObjectAggregateQuery.class)
                .matching(Query.query(createCriteria(filters)))
                .all();
    }

    private Criteria createCriteria(Map<String, Object> filters) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(filters.get("containerId"))) {
            criteria = criteria.and("containerId").is(new ObjectId((String) filters.get("containerId")));
        }
        if (Objects.nonNull(filters.get("key"))) {
            criteria = criteria.and("key").is((String) filters.get("key"));
        }

        Map<String, Object> metadataFilters = filters.entrySet().stream()
                .filter(e -> e.getKey().startsWith("metadata."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Map.Entry<String, Object> entry : metadataFilters.entrySet()) {
            criteria = criteria.and(entry.getKey()).is(entry.getValue());
        }
        return criteria;
    }

}
