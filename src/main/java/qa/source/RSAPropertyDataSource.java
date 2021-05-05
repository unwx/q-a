package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:properties/rsa.properties")
public class RSAPropertyDataSource {

    private final String RSA_PUBLIC_PATH;
    private final String RSA_PRIVATE_PATH;

    public RSAPropertyDataSource(@Value("${rsa.public.path}") String rsa_public_path,
                                 @Value("${rsa.private.path}") String rsa_private_path) {
        RSA_PUBLIC_PATH = rsa_public_path;
        RSA_PRIVATE_PATH = rsa_private_path;
    }

    public String getRSA_PUBLIC_PATH() {
        return RSA_PUBLIC_PATH;
    }

    public String getRSA_PRIVATE_PATH() {
        return RSA_PRIVATE_PATH;
    }
}
