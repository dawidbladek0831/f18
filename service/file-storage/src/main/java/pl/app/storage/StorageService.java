package pl.app.storage;

import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;


public interface StorageService {
    Mono<Void> init(ObjectId containerId);

    Mono<Void> create(ObjectId containerId, String storageId, byte[] content);

    Mono<Void> copy(ObjectId containerId, String storageId, String newStorageId);

    Mono<Void> delete(ObjectId containerId, String storageId);

    Mono<byte[]> get(ObjectId containerId, String storageId);
}
