package pl.app.object.application.port.in;

import org.bson.types.ObjectId;
import pl.app.object.application.domain.ObjectAggregate;
import reactor.core.publisher.Mono;

public interface ObjectDomainRepository {
    Mono<ObjectAggregate> fetchByContainerIdAndObjectKey(ObjectId containerId, String key);
}
