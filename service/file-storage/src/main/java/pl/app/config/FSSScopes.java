package pl.app.config;

import lombok.Getter;

@Getter
public enum FSSScopes {
    CONTAINER_READ("SCOPE_fss.containers:read"),
    CONTAINER_WRITE("SCOPE_fss.containers:write"),
    OBJECT_READ("SCOPE_fss.objects:read"),
    OBJECT_WRITE("SCOPE_fss.objects:write"),
    OBJECT_MANAGE("SCOPE_fss.objects:manage");
    private final String scopeName;

    FSSScopes(String scopeName) {
        this.scopeName = scopeName;
    }
}
