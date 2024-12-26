package pl.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import pl.app.common.shared.exception.AuthorizationException;
import pl.app.container.model.Container;
import pl.app.container.service.dto.ContainerDto;
import pl.app.object.application.domain.ObjectAggregate;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static pl.app.object.application.domain.ObjectAggregate.METADATA_OWNER_ID_KEY;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers/{name}").hasAuthority(FSSScopes.CONTAINER_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers").hasAuthority(FSSScopes.CONTAINER_READ.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/containers").hasAuthority(FSSScopes.CONTAINER_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/containers/{name}").hasAuthority(FSSScopes.CONTAINER_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/containers/{name}").hasAuthority(FSSScopes.CONTAINER_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers/{containerName}/objects/{key}").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers/{containerName}/objects").hasAuthority(FSSScopes.OBJECT_READ.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/containers/{containerName}/objects").hasAuthority(FSSScopes.OBJECT_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/containers/{containerName}/objects/{key}").hasAuthority(FSSScopes.OBJECT_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/containers/{containerName}/objects/{key}").hasAuthority(FSSScopes.OBJECT_WRITE.getScopeName())
                        .anyExchange().authenticated())
                .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()));
        return http.build();
    }

}
