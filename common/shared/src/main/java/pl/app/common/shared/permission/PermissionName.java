package pl.app.common.shared.permission;


public enum PermissionName {
    PERMISSION_READ("permission:read"),
    PERMISSION_WRITE("permission:write"),
    ROLE_READ("role:read"),
    ROLE_WRITE("role:write"),
    USER_READ("user:read"),
    USER_WRITE("user:write");
    private final String permission;

    PermissionName(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
