package qa.domain;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    USER, MODERATOR, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
