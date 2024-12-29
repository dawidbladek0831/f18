package pl.app.object.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pl.app.object.query.FileQueryService;
import pl.app.shared.PathVariableExtractor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(FileQueryRestController.resourcePath)
@RequiredArgsConstructor
class FileQueryRestController {
    public static final String resourceName = "files";
    public static final String resourcePath = "/api/v1/containers/{containerName}/" + resourceName;
    private final FileQueryService fileQueryService;

    @GetMapping(value = "/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ResponseEntity<Resource>> fetchFile(@PathVariable String containerName,
                                             @RequestParam(required = false) List<Integer> revision,
                                             ServerHttpRequest request) {
        String key = PathVariableExtractor.extractVariableAfterPath(resourcePath, request.getPath().pathWithinApplication().value());
        if (Objects.nonNull(revision) && !revision.isEmpty()) {
            return fileQueryService.fetchByContainerAndKeyAndRevisionAsResource(containerName, key, revision.getFirst())
                    .map(ResponseEntity::ok);
        }
        return fileQueryService.fetchByContainerAndKeyAsResource(containerName, key)
                .map(ResponseEntity::ok);
    }
}
