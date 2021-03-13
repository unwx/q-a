package qa.security.jwt.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

public class JwtAuthenticationData implements UserDetails {

    @Serial
    private static final long serialVersionUID = -8928621395986706152L;

    private final Long id;
    private final String password;
    private final String email;
    private Boolean enabled;
    private final Long accessTokenExpirationDateAtMills;
    private final Long refreshTokenExpirationDateAtMillis;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtAuthenticationData(Long id,
                                 String password,
                                 String email,
                                 Boolean enabled,
                                 Long accessTokenExpirationDateAtMills,
                                 Long refreshTokenExpirationDateAtMillis,
                                 Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.accessTokenExpirationDateAtMills = accessTokenExpirationDateAtMills;
        this.refreshTokenExpirationDateAtMillis = refreshTokenExpirationDateAtMillis;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Long getAccessTokenExpirationDateAtMills() {
        return accessTokenExpirationDateAtMills;
    }

    public Long getRefreshTokenExpirationDateAtMillis() {
        return refreshTokenExpirationDateAtMillis;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
