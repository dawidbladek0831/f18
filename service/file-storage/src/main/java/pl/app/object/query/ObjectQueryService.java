package pl.app.object.query;

import pl.app.object.query.dto.ObjectSimpleDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ObjectQueryService {
    <T> Flux<T> fetchAll(Class<T> dtoClass);

    <T> Flux<T> fetchByContainer(String containerName, Class<T> dtoClass);

    <T> Mono<T> fetchByContainerAndKey(String containerName, String key, Class<T> dtoClass);

    <T> Mono<T> fetchByContainerAndKeyAndRevision(String containerName, String key, Set<Integer> revision, Class<T> dtoClass);

}
