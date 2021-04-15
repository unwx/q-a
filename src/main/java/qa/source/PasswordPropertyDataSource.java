package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/password.properties")
public class PasswordPropertyDataSource {

    private final String ENCRYPTOR_PASSWORD_PATH;
    private final String REDIS_PASSWORD_PATH;

    public PasswordPropertyDataSource(@Value("${encoder.password.path}") String encryptor_password_path,
                                      @Value("${redis.password.path}") String redis_password_path) {
        ENCRYPTOR_PASSWORD_PATH = encryptor_password_path;
        REDIS_PASSWORD_PATH = redis_password_path;
    }

    public String getENCRYPTOR_PASSWORD_PATH() {
        return ENCRYPTOR_PASSWORD_PATH;
    }

    public String getREDIS_PASSWORD_PATH() {
        return REDIS_PASSWORD_PATH;
    }
}
