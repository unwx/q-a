package qa.security.jwt.entity;

import qa.domain.AuthenticationData;

public class JwtAuthenticationDataFactory {

    public JwtAuthenticationDataFactory() {}

    public static JwtAuthenticationData create(AuthenticationData data) {
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
