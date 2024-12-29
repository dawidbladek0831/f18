package pl.app.object.application.port.in;

import pl.app.container.service.dto.ContainerDto;
import pl.app.object.application.domain.ObjectAggregate;
import reactor.core.publisher.Mono;

interface RevisionPolicy {
    Mono<ObjectAggregate> createObject(ContainerDto container, ObjectCommand.CreateObjectCommand command);

    Mono<ObjectAggregate> updateObject(ContainerDto container, ObjectAggregate objectAggregate, ObjectCommand.UpdateObjectCommand command);

    Mono<ObjectAggregate> removeObject(ContainerDto container, ObjectAggregate objectAggregate, ObjectCommand.RemoveObjectCommand command);
}
