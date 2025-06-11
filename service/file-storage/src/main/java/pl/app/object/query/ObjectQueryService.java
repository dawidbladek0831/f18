package pl.app.object.query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

public interface ObjectQueryService {
    <T> Flux<T> fetchAll(Class<T> dtoClass);

    <T> Flux<T> fetchAll(String containerName, Map<String, Object> filters, Class<T> dtoClass);

    <T> Mono<T> fetchOne(String containerName, String key, Class<T> dtoClass);

    <T> Mono<T> fetchOne(String containerName, String key, Set<Integer> revision, Map<String, Object> filters, Class<T> dtoClass);
}
