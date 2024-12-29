package pl.app.object.adapter.in;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.app.object.adapter.in.dto.CreateObjectBase64Dto;
import pl.app.object.adapter.in.dto.UpdateObjectBase64Dto;
import pl.app.object.application.domain.ObjectAggregate;
import pl.app.object.application.port.in.ObjectCommand;
import pl.app.object.application.port.in.ObjectService;
import pl.app.object.query.ObjectMapper;
import pl.app.object.query.dto.ObjectSimpleDto;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping(ObjectRestController.resourcePath)
@RequiredArgsConstructor
class ObjectRestController {
    public static final String resourceName = "objects";
    public static final String resourcePath = "/api/v1/containers/{containerName}/" + resourceName;

    private final ObjectService service;
    private final ObjectMapper objectMapper;

    @PostMapping
    Mono<ResponseEntity<ObjectSimpleDto>> crate(@PathVariable String containerName, @RequestBody CreateObjectBase64Dto dto) {
        return ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.getAuthentication().getPrincipal()).cast(Jwt.class)
                .flatMap(jwt -> {
                    String subjectId = jwt.getSubject();
                    dto.getMetadata().put(ObjectAggregate.METADATA_OWNER_ID_KEY, subjectId);
                    return service.crate(new ObjectCommand.CreateObjectCommand(dto.getKey(), containerName, decodeContent(dto.getContent()), dto.getMetadata()))
                            .map(e -> objectMapper.map(e, ObjectSimpleDto.class))
                            .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(e));
                });
    }

    @PutMapping("/{key}")
    Mono<ResponseEntity<ObjectSimpleDto>> update(@PathVariable String containerName, @PathVariable String key,
                                                 @RequestBody(required = false) UpdateObjectBase64Dto dto,
                                                 @RequestParam(required = false) List<Integer> revision) {
        if (Objects.nonNull(revision) && !revision.isEmpty()) {
            return service.restoreRevision(new ObjectCommand.RestoreObjectRevisionCommand(key, containerName, revision.getFirst()))
                    .map(e -> objectMapper.map(e, ObjectSimpleDto.class))
                    .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
        }
        return service.update(new ObjectCommand.UpdateObjectCommand(key, containerName, decodeContent(dto.getContent()), dto.getMetadata()))
                .map(e -> objectMapper.map(e, ObjectSimpleDto.class))
                .map(e -> ResponseEntity.status(HttpStatus.OK).body(e));
    }

    @DeleteMapping("/{key}")
    Mono<ResponseEntity<Void>> remove(@PathVariable String containerName, @PathVariable String key,
                                      @RequestParam(required = false) Set<Integer> revision) {
        if (Objects.nonNull(revision) && !revision.isEmpty()) {
            return service.deleteRevision(new ObjectCommand.DeleteObjectRevisionCommand(key, containerName, revision))
                    .then(Mono.just(ResponseEntity.noContent().build()));
        }
        return service.remove(new ObjectCommand.RemoveObjectCommand(key, containerName))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    private byte[] decodeContent(String base64) {
        String content = base64.replaceFirst("^(?=.{0,100}base64,).*?base64,", ""); // remove prefix like: data:image/jpeg;base64,
        return Base64.getDecoder().decode(content);
    }
}
