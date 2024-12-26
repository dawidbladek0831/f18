package pl.app.container.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.app.container.service.ContainerQueryService;
import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ContainerQueryRestController.resourcePath)
@RequiredArgsConstructor
class ContainerQueryRestController {
    public static final String resourceName = "containers";
    public static final String resourcePath = "/api/v1/" + resourceName;

    private final ContainerQueryService queryService;

    @GetMapping("/{name}")
    Mono<ResponseEntity<ContainerDto>> fetchByName(@PathVariable String name) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(xontext -> queryService.fetchByName(name)
                .map(ResponseEntity::ok));
//        return queryService.fetchByName(name)
//                .map(ResponseEntity::ok);
    }

    @GetMapping
    Mono<ResponseEntity<Flux<ContainerDto>>> fetchAll() {
        return Mono.just(ResponseEntity.ok(queryService.fetchAll()));
    }
}
