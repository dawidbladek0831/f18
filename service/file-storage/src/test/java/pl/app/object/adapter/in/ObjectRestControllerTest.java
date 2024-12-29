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
import pl.app.object.adapter.in.dto.UpdateObjectBase64Dto;
import pl.app.object.application.port.in.ObjectService;

import java.util.HashMap;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ObjectRestControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private ObjectService objectService;

    @Test
    void crate_shouldCreate_whenContainerExistsAndUserHasWritePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void crate_shouldCreate_whenContainerExistsAndUserHasManagePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PUBLIC, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void update_shouldUpdate_whenContainerOfTypeProtectedAndUserHasWritePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .put().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .bodyValue(new UpdateObjectBase64Dto("content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void update_shouldUpdateByRevision_whenContainerOfTypeProtectedAndUserHasWritePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .put().uri("/api/v1/containers/{containerName}/objects/{key}?revision={revision}", containerName, objectKey, 1)
                .bodyValue(new UpdateObjectBase64Dto("content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void update_shouldUpdate_whenContainerOfTypePrivateAndUserHasWritePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .put().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .bodyValue(new UpdateObjectBase64Dto("content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void update_shouldThrow_whenContainerOfTypePrivateAndUserHasWritePermissionButUserIsNotOwner() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var userId2 = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId2).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .put().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .bodyValue(new UpdateObjectBase64Dto("content", new HashMap<>()))
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void update_shouldUpdate_whenContainerOfTypePrivateAndUserHasManagePermissionAndUserIsNotOwner() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var userId2 = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId2).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()))))
                .put().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .bodyValue(new UpdateObjectBase64Dto("content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void remove_shouldRemove_whenContainerOfTypeProtectedAndUserHasWritePermission() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PROTECTED, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .delete().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void remove_shouldRemove_whenContainerOfTypePrivateAndUserHasWritePermissionAndUserIsOwner() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .delete().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void remove_shouldThrow_whenContainerOfTypePrivateAndUserHasWritePermissionButUserIsNotOwner() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        var userId2 = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId2).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .delete().uri("/api/v1/containers/{containerName}/objects/{key}", containerName, objectKey)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void remove_shouldRemoveRevision_whenContainerOfTypePrivateAndUserHasWritePermissionAndUserIsOwner() {
        var containerName = ObjectId.get().toString();
        var objectKey = ObjectId.get().toString();
        var userId = ObjectId.get().toString();
        containerService.create(new ContainerDto(null, containerName, Container.ContainerType.PRIVATE, RevisionPolicyType.REVISION_ON)).block();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .post().uri("/api/v1/containers/{containerName}/objects", containerName)
                .bodyValue(new CreateObjectBase64Dto(objectKey, "content", new HashMap<>()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", userId).build())
                        .authorities(List.of(new SimpleGrantedAuthority(FSSScopes.OBJECT_WRITE.getScopeName()))))
                .delete().uri("/api/v1/containers/{containerName}/objects/{key}?revision={revision}", containerName, objectKey, 1)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}