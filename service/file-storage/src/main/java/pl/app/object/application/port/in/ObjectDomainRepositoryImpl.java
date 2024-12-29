package pl.app.object.application.port.in;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import pl.app.object.application.domain.ObjectAggregate;
import pl.app.object.application.domain.ObjectException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class ObjectDomainRepositoryImpl implements ObjectDomainRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<ObjectAggregate> fetchByContainerIdAndObjectKey(ObjectId containerId, String key) {
        return mongoTemplate.query(ObjectAggregate.class)
                .matching(Query.query(Criteria.where("containerId").is(containerId).and("key").is(key)))
                .one()
                .switchIfEmpty(Mono.error(() -> ObjectException.NotFoundObjectException.key(key)));
    }

}
