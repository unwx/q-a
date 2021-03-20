package qa.security.jwt.entity;

import org.jetbrains.annotations.Nullable;
import qa.domain.AuthenticationData;

public class JwtAuthenticationDataFactory {

    public JwtAuthenticationDataFactory() {}

    @Nullable
    public static JwtAuthenticationData create(@Nullable AuthenticationData data) {
        if (data == null)
            return null;
        return new JwtAuthenticationData(
                data.getId(),
                data.getPassword(),
                data.getEmail(),
                data.getEnabled(),
                data.getAccessTokenExpirationDateAtMills(),
                data.getRefreshTokenExpirationDateAtMillis(),
                data.getRoles()
        );
    }
}
