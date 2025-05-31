package pl.app.object.query;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import pl.app.config.AuthorizationService;
import pl.app.config.FSSScopes;
import pl.app.container.service.ContainerQueryService;
import pl.app.object.application.domain.ObjectException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.app.object.application.domain.ObjectAggregate.METADATA_OWNER_ID_KEY;

@Service
@RequiredArgsConstructor
class ObjectQueryServiceImpl implements ObjectQueryService {
    private final ObjectMapper mapper;
    private final ReactiveMongoTemplate mongoTemplate;

    private final ContainerQueryService containerQueryService;

    @Override
    public <T> Flux<T> fetchAll(Class<T> dtoClass) {
        return AuthorizationService.verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName())
                .thenMany(mongoTemplate.query(ObjectAggregateQuery.class).all()
                        .map(e -> mapper.map(e, dtoClass)));
    }

    @Override
    public <T> Flux<T> fetchByContainer(String containerName, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> AuthorizationService.verifySubjectHasAuthorityToReadObjectsInContainer(container).thenReturn(container))
                .flatMapMany(container -> mongoTemplate.query(ObjectAggregateQuery.class)
                        .matching(Query.query(Criteria.where("containerId").is(container.getContainerId())))
                        .all()
                        .map(e -> mapper.map(e, dtoClass))
                );
    }

    @Override
    public <T> Flux<T> fetchByContainer(String containerName, Class<T> dtoClass, Map<String, String> filters) {
        Map<String, String> metadataFilters = filters.entrySet().stream()
                .filter(e -> e.getKey().startsWith("metadata."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> AuthorizationService.verifySubjectHasAuthorityToReadObjectsInContainer(container).thenReturn(container))
                .flatMapMany(container -> {
                            Criteria criteria = Criteria.where("containerId").is(container.getContainerId());
                            for (Map.Entry<String, String> entry : metadataFilters.entrySet()) {
                                criteria = criteria.and(entry.getKey()).is(entry.getValue());
                            }
                            return mongoTemplate.query(ObjectAggregateQuery.class)
                                    .matching(Query.query(criteria))
                                    .all()
                                    .map(e -> mapper.map(e, dtoClass));
                        }
                );
    }

    @Override
    public <T> Mono<T> fetchByContainerAndKey(String containerName, String key, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> mongoTemplate.query(ObjectAggregateQuery.class)
                        .matching(Query.query(
                                Criteria.where("containerId").is(container.getContainerId()).and("key").is(key)
                        )).one()
                        .flatMap(domain -> AuthorizationService.verifySubjectHasAuthorityToReadObjectInContainer(container, (String) domain.getMetadata().get(METADATA_OWNER_ID_KEY)).thenReturn(domain))
                        .map(e -> mapper.map(e, dtoClass))
                        .switchIfEmpty(Mono.error(() -> ObjectException.NotFoundObjectException.key(key)))
                );
    }

    @Override
    public <T> Mono<T> fetchByContainerAndKeyAndRevision(String containerName, String key, Set<Integer> revision, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> mongoTemplate.query(ObjectAggregateQuery.class)
                        .matching(Query.query(
                                Criteria.where("containerId").is(container.getContainerId())
                                        .and("key").is(key)
                                        .and("revisions.revisionId").in(revision)
                        )).one()
                        .map(domain -> {
                            Set<ObjectAggregateQuery.Revision> filteredRevisions = domain.getRevisions().stream()
                                    .filter(e -> revision.contains(e.getRevisionId()))
                                    .collect(Collectors.toSet());
                            domain.setRevisions(filteredRevisions);
                            return domain;
                        })
                        .flatMap(domain -> AuthorizationService.verifySubjectHasAuthorityToReadObjectInContainer(container, (String) domain.getMetadata().get(METADATA_OWNER_ID_KEY)).thenReturn(domain))
                        .map(e -> mapper.map(e, dtoClass))
                        .switchIfEmpty(Mono.error(() -> ObjectException.NotFoundObjectException.key(key)))
                );
    }
}
