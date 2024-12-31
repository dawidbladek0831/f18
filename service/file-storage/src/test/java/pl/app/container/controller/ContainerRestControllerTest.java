package pl.app.container.controller;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.FSSScopes;
import pl.app.container.model.Container;
import pl.app.container.model.RevisionPolicyType;
import pl.app.container.service.ContainerService;
import pl.app.container.service.dto.ContainerDto;
import pl.app.storage.StorageService;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ContainerRestControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private StorageService storageService;

    @Test
    void create_shouldCreateContainer_whenUserHasPermissions() {
        var containerName = ObjectId.get().toString();
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.CONTAINER_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers")
                .bodyValue(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(containerName);
    }

    @Test
    void create_shouldThrow_WhenThereIsContainerWithGivenName() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON))
                .flatMap(container -> storageService.init(container.getContainerId()))
                .block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.CONTAINER_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers")
                .bodyValue(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void create_shouldThrow_WhenUserHasNoPermissions() {
        var containerName = ObjectId.get().toString();
        webTestClient
                .post().uri("/api/v1/containers")
                .bodyValue(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void update_shouldUpdate_whenContainerExists() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON))
                .flatMap(container -> storageService.init(container.getContainerId()))
                .block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.CONTAINER_WRITE.getScopeName()))))
                .put().uri("/api/v1/containers/{containerName}", containerName)
                .bodyValue(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_OFF))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void delete_shouldDelete_whenContainerExists() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON))
                .flatMap(container -> storageService.init(container.getContainerId()))
                .block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.CONTAINER_WRITE.getScopeName()))))
                .delete().uri("/api/v1/containers/{containerName}", containerName)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}