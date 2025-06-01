package pl.app.config;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;
import pl.app.common.shared.exception.AuthorizationException;
import pl.app.container.model.Container;
import pl.app.container.service.dto.ContainerDto;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class AuthorizationService {
    public static Mono<String> subjectId() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(AuthorizationException::new))
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .map(JwtClaimAccessor::getSubject);
    }

    public static Mono<Void> verifySubjectIsOwner(String subjectId) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(AuthorizationException::new))
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .flatMap(jwt -> {
                    String id = jwt.getSubject();
                    if (Objects.isNull(subjectId) || Objects.isNull(id)) {
                        return Mono.error(AuthorizationException::new);
                    }
                    return subjectId.equals(id) ? Mono.empty() : Mono.error(AuthorizationException::new);
                });
    }

    public static Mono<Void> verifySubjectHasAuthority(String authority) {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(AuthorizationException::new))
                .flatMap(context -> context.getAuthentication().getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(authority)) ? Mono.empty() : Mono.error(AuthorizationException::new));
    }

    public static Mono<Void> verifySubjectHasAuthorityToEditObjectInContainer(ContainerDto container, String subjectId) {
        if (Container.ContainerType.PUBLIC.equals(container.getType())) {
            return Mono.empty();
        }
        if (Container.ContainerType.PROTECTED.equals(container.getType())) {
            return verifySubjectHasAuthority(FSSScopes.OBJECT_WRITE.getScopeName());
        }
        return verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName())
                .onErrorResume(e -> verifySubjectIsOwner(subjectId));
    }

    public static Mono<Void> verifySubjectHasAuthorityToReadObjectInContainer(ContainerDto container, String subjectId) {
        if (Container.ContainerType.PUBLIC.equals(container.getType())) {
            return Mono.empty();
        }
        if (Container.ContainerType.PROTECTED.equals(container.getType())) {
            return verifySubjectHasAuthority(FSSScopes.OBJECT_READ.getScopeName())
                    .onErrorResume(e -> verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName()));
        }
        return verifySubjectHasAuthority(FSSScopes.OBJECT_MANAGE.getScopeName())
                .onErrorResume(e -> verifySubjectIsOwner(subjectId));
    }
}
