package qa.source.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/validation/validAuthentication.properties")
public class AuthenticationPropertyDataSource {

    private final Integer AUTHENTICATION_PASSWORD_LENGTH_MAX;
    private final Integer AUTHENTICATION_PASSWORD_LENGTH_MIN;

    private AuthenticationPropertyDataSource(@Value("${authentication.password.length.max}") Integer authentication_password_length_max,
                                             @Value("${authentication.password.length.min}") Integer authentication_password_length_min) {
        AUTHENTICATION_PASSWORD_LENGTH_MAX = authentication_password_length_max;
        AUTHENTICATION_PASSWORD_LENGTH_MIN = authentication_password_length_min;
    }

    public Integer getAUTHENTICATION_PASSWORD_LENGTH_MAX() {
        return AUTHENTICATION_PASSWORD_LENGTH_MAX;
    }

    public Integer getAUTHENTICATION_PASSWORD_LENGTH_MIN() {
        return AUTHENTICATION_PASSWORD_LENGTH_MIN;
    }
}
