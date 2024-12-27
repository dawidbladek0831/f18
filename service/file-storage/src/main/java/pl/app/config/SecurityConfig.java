package pl.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtAuthenticationConverter JwtAuthenticationConverter) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers/{name}").hasAuthority(FSSScopes.CONTAINER_READ.getScopeName())
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers").hasAuthority(FSSScopes.CONTAINER_READ.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/containers").hasAuthority(FSSScopes.CONTAINER_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/containers/{name}").hasAuthority(FSSScopes.CONTAINER_WRITE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/containers/{name}").hasAuthority(FSSScopes.CONTAINER_WRITE.getScopeName())

                        .pathMatchers(HttpMethod.GET, "/api/v1/containers/{containerName}/objects/{key}").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/containers/{containerName}/objects").hasAnyAuthority(FSSScopes.OBJECT_READ.getScopeName(), FSSScopes.OBJECT_MANAGE.getScopeName())
                        .pathMatchers(HttpMethod.POST, "/api/v1/containers/{containerName}/objects").hasAnyAuthority(FSSScopes.OBJECT_WRITE.getScopeName(), FSSScopes.OBJECT_MANAGE.getScopeName())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/containers/{containerName}/objects/{key}").hasAnyAuthority(FSSScopes.OBJECT_WRITE.getScopeName(), FSSScopes.OBJECT_MANAGE.getScopeName())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/containers/{containerName}/objects/{key}").hasAnyAuthority(FSSScopes.OBJECT_WRITE.getScopeName(), FSSScopes.OBJECT_MANAGE.getScopeName())
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(c -> c
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(JwtAuthenticationConverter))));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter JwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        return jwtGrantedAuthoritiesConverter;
    }
}
