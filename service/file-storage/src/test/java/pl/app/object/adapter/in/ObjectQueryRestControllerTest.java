package pl.app.object.adapter.in;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.app.common.shared.test.AbstractIntegrationTest;
import pl.app.config.FSSScopes;
import pl.app.container.model.Container;
import pl.app.container.model.RevisionPolicyType;
import pl.app.container.service.ContainerService;
import pl.app.container.service.dto.ContainerDto;
import pl.app.object.adapter.in.dto.CreateObjectBase64Dto;
import pl.app.object.application.port.in.ObjectCommand;
import pl.app.object.application.port.in.ObjectService;

import java.util.HashMap;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ObjectQueryRestControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ContainerService containerService;
    @Autowired
    private ObjectService objectService;

    @Test
    void fetchByContainer_shouldFetch_whenContainerOfPublicTypeAndUserHasPermissions() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_READ.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}/objects", containerName)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fetchByContainer_shouldFetch_whenContainerOfProtectedTypeAndUserHasPermissions() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_READ.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}/objects", containerName)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fetchByContainer_shouldFetch_whenContainerOfPrivateTypeAndUserHasPermissions() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}/objects", containerName)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fetchByContainer_shouldThrow_whenContainerOfPrivateTypeAndUserHasOnlyReadPermission() {
        var containerName = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_READ.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}/objects", containerName)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void fetchByContainerAndKey_shouldFetch_whenObjectExistsAndContainerOfTypePublic() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();
        objectService.crate(new ObjectCommand.CreateObjectCommand(objectKey, containerName, new byte[]{}, new HashMap<>())).block();

        webTestClient
                .get().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fetchByContainerAndKey_shouldFetch_whenObjectExistsAndContainerOfTypeProtectedAndUserHasReadPermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();
        objectService.crate(new ObjectCommand.CreateObjectCommand(objectKey, containerName, new byte[]{}, new HashMap<>())).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_READ.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fetchByContainerAndKey_shouldThrow_whenObjectExistsAndContainerOfTypeProtectedAndUserHasNoReadPermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();
        objectService.crate(new ObjectCommand.CreateObjectCommand(objectKey, containerName, new byte[]{}, new HashMap<>())).block();

        webTestClient
                .get().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void fetchByContainerAndKey_shouldFetch_whenObjectExistsAndContainerOfTypePrivateAndUserHasManagePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()))))
                .get().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}