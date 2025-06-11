package pl.app.object.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pl.app.object.query.ObjectDtoName;
import pl.app.object.query.ObjectQueryService;
import pl.app.shared.PathVariableExtractor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(ObjectQueryRestController.resourcePath)
@RequiredArgsConstructor
class ObjectQueryRestController {
    public static final String resourceName = "objects";
    public static final String resourcePath = "/api/v1/containers/{containerName}/" + resourceName;

    private final ObjectQueryService queryService;


    @GetMapping
    Mono<ResponseEntity<Flux<?>>> fetchAll(@PathVariable String containerName,
                                           @RequestParam(required = false) String dto,
                                           @RequestParam Map<String, Object> queryParams) {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll(containerName, queryParams, ObjectDtoName.fromString(dto).getDtoClass())));
    }

    @GetMapping("/**")
    Mono<ResponseEntity<?>> fetchOne(@PathVariable String containerName,
                                     @RequestParam(required = false) Set<Integer> revision,
                                     @RequestParam(required = false) String dto,
                                     @RequestParam Map<String, Object> queryParams,
                                     ServerHttpRequest request) {
        String key = PathVariableExtractor.extractVariableAfterPath(resourcePath, request.getPath().pathWithinApplication().value());
        return queryService.fetchOne(containerName, key, revision, queryParams, ObjectDtoName.fromString(dto).getDtoClass())
                .map(ResponseEntity::ok);
    }


}
