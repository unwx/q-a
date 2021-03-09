package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources({
        @PropertySource("classpath:properties/validation/validAuthentication.properties"),
        @PropertySource("classpath:properties/validation/validUser.properties"),
        @PropertySource("classpath:properties/rsa.properties"),
        @PropertySource("classpath:properties/jwt.properties")
})
public final class PropertiesDataSource {

    public PropertiesDataSource() {
    }

    /* authentication validation properties */

    @Value("${authentication.password.length.max}")
    private Integer AUTHENTICATION_PASSWORD_LENGTH_MAX;

    @Value("${authentication.password.length.min}")
    private Integer AUTHENTICATION_PASSWORD_LENGTH_MIN;

    /* user validation properties */

    @Value("${user.username.length.max}")
    private Integer USER_USERNAME_LENGTH_MAX;

    @Value("${user.username.length.min}")
    private Integer USER_USERNAME_LENGTH_MIN;

    @Value("${user.about.length.max}")
    private Integer USER_ABOUT_LENGTH_MAX;

    @Value("${user.about.length.min}")
    private Integer USER_ABOUT_LENGTH_MIN;

    /* rsa properties */

    @Value("${rsa.public.path}")
    private String RSA_PUBLIC_PATH;

    @Value("${rsa.private.path}")
    private String RSA_PRIVATE_PATH;

    /* jwt properties */

    @Value("${jwt.algorithm}")
    private String JWT_ALGORITHM;

    @Value("${jwt.access.expiration}")
    private Long JWT_ACCESS_EXPIRATION;

    @Value("${jwt.refresh.expiration}")
    private Long JWT_REFRESH_EXPIRATION;


    public Integer getAUTHENTICATION_PASSWORD_LENGTH_MAX() {
        return AUTHENTICATION_PASSWORD_LENGTH_MAX;
    }

    public Integer getAUTHENTICATION_PASSWORD_LENGTH_MIN() {
        return AUTHENTICATION_PASSWORD_LENGTH_MIN;
    }

    public Integer getUSER_USERNAME_LENGTH_MAX() {
        return USER_USERNAME_LENGTH_MAX;
    }

    public Integer getUSER_USERNAME_LENGTH_MIN() {
        return USER_USERNAME_LENGTH_MIN;
    }

    public Integer getUSER_ABOUT_LENGTH_MAX() {
        return USER_ABOUT_LENGTH_MAX;
    }

    public Integer getUSER_ABOUT_LENGTH_MIN() {
        return USER_ABOUT_LENGTH_MIN;
    }

    public String getRSA_PUBLIC_PATH() {
        return RSA_PUBLIC_PATH;
    }

    public String getRSA_PRIVATE_PATH() {
        return RSA_PRIVATE_PATH;
    }

    public String getJWT_ALGORITHM() {
        return JWT_ALGORITHM;
    }

    public Long getJWT_ACCESS_EXPIRATION() {
        return JWT_ACCESS_EXPIRATION;
    }

    public Long getJWT_REFRESH_EXPIRATION() {
        return JWT_REFRESH_EXPIRATION;
    }
}
