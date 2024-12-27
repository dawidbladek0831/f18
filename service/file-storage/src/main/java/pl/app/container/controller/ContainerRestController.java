package pl.app.container.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pl.app.container.service.ContainerService;
import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ContainerRestController.resourcePath)
@RequiredArgsConstructor
class ContainerRestController {
    public static final String resourceName = "containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ContainerService service;

    @PostMapping
    Mono<ResponseEntity<ContainerDto>> crate(@RequestBody ContainerDto dto) {
        return service.create(dto)
                .map(e -> ResponseEntity
                        .created(UriComponentsBuilder.fromUriString(resourcePath).path("/{name}").buildAndExpand(e.getName()).toUri())
                        .body(e)
                );
    }

    @PutMapping("/{name}")
    Mono<ResponseEntity<ContainerDto>> update(@PathVariable String name, @RequestBody ContainerDto dto) {
        return service.update(name, dto)
                .map(e -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(e)
                );
    }

    @DeleteMapping("/{name}")
    Mono<ResponseEntity<Void>> delete(@PathVariable String name) {
        return service.deleteByName(name)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
