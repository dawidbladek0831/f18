package pl.app.container.service;

import jakarta.validation.Valid;
import org.springframework.lang.NonNull;
import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Mono;

public interface ContainerService {
    Mono<ContainerDto> create(@Valid ContainerDto dto);

    Mono<ContainerDto> update(@NonNull String name, ContainerDto dto);

    Mono<Void> deleteByName(@NonNull String name);
}
