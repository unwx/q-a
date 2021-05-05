package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/jwt.properties")
public class JWTPropertyDataSource {

    private final Long JWT_ACCESS_EXPIRATION;
    private final Long JWT_REFRESH_EXPIRATION;

    public JWTPropertyDataSource(@Value("${jwt.access.expiration}") Long jwt_access_expiration,
                                 @Value("${jwt.refresh.expiration}") Long jwt_refresh_expiration) {
        JWT_ACCESS_EXPIRATION = jwt_access_expiration;
        JWT_REFRESH_EXPIRATION = jwt_refresh_expiration;
    }

    public Long getJWT_ACCESS_EXPIRATION() {
        return JWT_ACCESS_EXPIRATION;
    }

    public Long getJWT_REFRESH_EXPIRATION() {
        return JWT_REFRESH_EXPIRATION;
    }
}
