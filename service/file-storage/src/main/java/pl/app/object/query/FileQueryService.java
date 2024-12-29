package pl.app.object.query;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;


public interface FileQueryService {
    Mono<byte[]> fetchByContainerAndKey(String containerName, String key);

    Mono<byte[]> fetchByContainerAndKeyAndRevision(String containerName, String key, Integer revision);

    Mono<Resource> fetchByContainerAndKeyAsResource(String containerName, String key);

    Mono<Resource> fetchByContainerAndKeyAndRevisionAsResource(String containerName, String key, Integer revision);
}
