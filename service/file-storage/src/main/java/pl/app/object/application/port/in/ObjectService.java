package pl.app.object.application.port.in;

import pl.app.object.application.domain.ObjectAggregate;
import reactor.core.publisher.Mono;

public interface ObjectService {
    Mono<ObjectAggregate> crate(ObjectCommand.CreateObjectCommand command);

    Mono<ObjectAggregate> update(ObjectCommand.UpdateObjectCommand command);

    Mono<ObjectAggregate> remove(ObjectCommand.RemoveObjectCommand command);

    Mono<ObjectAggregate> deleteRevision(ObjectCommand.DeleteObjectRevisionCommand command);

    Mono<ObjectAggregate> restoreRevision(ObjectCommand.RestoreObjectRevisionCommand command);
}
