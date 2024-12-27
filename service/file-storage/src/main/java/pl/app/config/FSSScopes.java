package pl.app.config;

import lombok.Getter;

@Getter
public enum FSSScopes {
    CONTAINER_READ("fss.containers:read"),
    CONTAINER_WRITE("fss.containers:write"),
    OBJECT_READ("fss.objects:read"),
    OBJECT_WRITE("fss.objects:write"),
    OBJECT_MANAGE("fss.objects:manage");
    private final String scopeName;

    FSSScopes(String scopeName) {
        this.scopeName = scopeName;
    }
}
