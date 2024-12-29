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

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping(ObjectQueryRestController.resourcePath)
@RequiredArgsConstructor
class ObjectQueryRestController {
    public static final String resourceName = "objects";
    public static final String resourcePath = "/api/v1/containers/{containerName}/" + resourceName;

    private final ObjectQueryService queryService;


    @GetMapping
    Mono<ResponseEntity<Flux<?>>> fetchByContainer(@PathVariable String containerName,
                                                   @RequestParam(required = false) String dto) {
        return Mono.just(ResponseEntity.ok(queryService.fetchByContainer(containerName, ObjectDtoName.fromString(dto).getDtoClass())));
    }

    @GetMapping("/**")
    Mono<ResponseEntity<?>> fetchByContainerAndKey(@PathVariable String containerName,
                                                   @RequestParam(required = false) Set<Integer> revision,
                                                   @RequestParam(required = false) String dto,
                                                   ServerHttpRequest request) {
        String key = PathVariableExtractor.extractVariableAfterPath(resourcePath, request.getPath().pathWithinApplication().value());
        if (Objects.nonNull(revision) && !revision.isEmpty()) {
            return queryService.fetchByContainerAndKeyAndRevision(containerName, key, revision, ObjectDtoName.fromString(dto).getDtoClass())
                    .map(ResponseEntity::ok);
        }
        return queryService.fetchByContainerAndKey(containerName, key, ObjectDtoName.fromString(dto).getDtoClass())
                .map(ResponseEntity::ok);
    }


}
