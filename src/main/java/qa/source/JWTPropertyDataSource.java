package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/jwt.properties")
public class JWTPropertyDataSource {

    @Value("${jwt.access.expiration}")
    private Long JWT_ACCESS_EXPIRATION;

    @Value("${jwt.refresh.expiration}")
    private Long JWT_REFRESH_EXPIRATION;

    public Long getJWT_ACCESS_EXPIRATION() {
        return JWT_ACCESS_EXPIRATION;
    }

    public Long getJWT_REFRESH_EXPIRATION() {
        return JWT_REFRESH_EXPIRATION;
    }
}
