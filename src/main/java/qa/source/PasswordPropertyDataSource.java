package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/password.properties")
public class PasswordPropertyDataSource {

    private final String PASSWORD_PATH;

    public PasswordPropertyDataSource(@Value("${password.path}") String password_path) {
        PASSWORD_PATH = password_path;
    }

    public String getPASSWORD_PATH() {
        return PASSWORD_PATH;
    }
}
