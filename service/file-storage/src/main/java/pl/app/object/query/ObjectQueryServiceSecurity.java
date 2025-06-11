package pl.app.object.query;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.app.config.AuthorizationService;
import pl.app.config.FSSScopes;
import pl.app.container.service.ContainerQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pl.app.config.AuthorizationService.verifySubjectHasAuthority;

@Service
@RequiredArgsConstructor
@Primary
class ObjectQueryServiceSecurity implements ObjectQueryService {
    private final ObjectQueryService wrappedService;

    private final ContainerQueryService containerQueryService;

    @Override
    public <T> Flux<T> fetchAll(Class<T> dtoClass) {
        return verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName())
                .thenMany(wrappedService.fetchAll(dtoClass));
    }

    @Override
    public <T> Flux<T> fetchAll(String containerName, Map<String, Object> filters, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMapMany(container -> switch (container.getType()) {
                    case PUBLIC -> wrappedService.fetchAll(containerName, filters, dtoClass);
                    case PROTECTED -> verifySubjectHasAuthority(FSSScopes.OBJECT_READ.getScopeName())
                            .thenMany(wrappedService.fetchAll(containerName, filters, dtoClass));
                    case PRIVATE -> verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName())
                            .thenMany(wrappedService.fetchAll(containerName, filters, dtoClass))
                            .onErrorResume(e -> verifySubjectHasAuthority(FSSScopes.OBJECT_READ.getScopeName())
                                    .then(AuthorizationService.subjectId())
                                    .flatMapMany(subjectId -> {
                                        filters.put("metadata.ownerId", subjectId);
                                        return wrappedService.fetchAll(containerName, filters, dtoClass);
                                    })
                            );
                });
    }

    @Override
    public <T> Mono<T> fetchOne(String containerName, String key, Class<T> dtoClass) {
        return fetchOne(containerName, key, new HashSet<>(), new HashMap<>(), dtoClass);
    }

    @Override
    public <T> Mono<T> fetchOne(String containerName, String key, Set<Integer> revision, Map<String, Object> filters, Class<T> dtoClass) {
        return containerQueryService.fetchByName(containerName)
                .flatMap(container -> switch (container.getType()) {
                    case PUBLIC -> wrappedService.fetchOne(containerName, key, revision, filters, dtoClass);
                    case PROTECTED -> verifySubjectHasAuthority(FSSScopes.OBJECT_READ.getScopeName())
                            .then(wrappedService.fetchOne(containerName, key, revision, filters, dtoClass));
                    case PRIVATE -> verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName())
                            .then(wrappedService.fetchOne(containerName, key, revision, filters, dtoClass))
                            .onErrorResume(e -> verifySubjectHasAuthority(FSSScopes.OBJECT_READ.getScopeName())
                                    .then(AuthorizationService.subjectId())
                                    .flatMap(subjectId -> {
                                        filters.put("metadata.ownerId", subjectId);
                                        return wrappedService.fetchOne(containerName, key, revision, filters, dtoClass);
                                    })
                            );
                });
    }
}
