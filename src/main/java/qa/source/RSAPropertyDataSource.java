package qa.source;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources(
        @PropertySource("classpath:properties/rsa.properties")
)
public class RSAPropertyDataSource {

    @Value("${rsa.public.path}")
    private String RSA_PUBLIC_PATH;

    @Value("${rsa.private.path}")
    private String RSA_PRIVATE_PATH;

    public String getRSA_PUBLIC_PATH() {
        return RSA_PUBLIC_PATH;
    }

    public String getRSA_PRIVATE_PATH() {
        return RSA_PRIVATE_PATH;
    }
}
