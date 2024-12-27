package pl.app.container.service;

import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContainerQueryService {
    Flux<ContainerDto> fetchAll();

    Mono<ContainerDto> fetchByName(String containerName);
}
