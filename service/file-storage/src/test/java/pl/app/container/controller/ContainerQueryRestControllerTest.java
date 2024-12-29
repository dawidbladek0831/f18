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

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ContainerQueryRestControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ContainerService containerService;

    @Test
    void fetchByName_shouldFetch_whenUserHasPermissions() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.CONTAINER_READ.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}", containerName)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fetchByName_shouldThrow_whenUserHasNoPermissions() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .get().uri("/api/v1/containers/{containerName}", containerName)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void fetchAll_shouldFetch_whenUserHasPermissions() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.CONTAINER_READ.getScopeName()))))
                .get().uri("/api/v1/containers")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}