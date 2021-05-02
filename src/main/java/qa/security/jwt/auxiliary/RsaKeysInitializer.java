package qa.security.jwt.auxiliary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.security.jwt.auxiliary.util.PemUtil;
import qa.source.RSAPropertyDataSource;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class RsaKeysInitializer {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private final RSAPropertyDataSource propertiesDataSource;

    private static final Logger logger = LogManager.getLogger(RsaKeysInitializer.class);
    private static final String ERR_PUBLIC =
                        """
                        cannot get the PUBLIC RSA key from the file.\s\
                        filepath: %s
                        Cause: %s\
                        """;
    private static final String ERR_PRIVATE =
                        """
                        cannot get the PUBLIC RSA key from the file.\s\
                        filepath: %s
                        Cause: %s\
                        """;

    @Autowired
    public RsaKeysInitializer(RSAPropertyDataSource propertiesDataSource) {
        this.propertiesDataSource = propertiesDataSource;
    }

    public RSAPublicKey getPublicKey() {
        if (this.publicKey == null) {
            try {
                this.publicKey = (RSAPublicKey) PemUtil.readPublicKeyFromFile(this.propertiesDataSource.getRSA_PUBLIC_PATH(), "RSA");
            } catch (IOException e) {
                final String log = ERR_PUBLIC.formatted(this.propertiesDataSource.getRSA_PUBLIC_PATH(), e.getMessage());
                e.printStackTrace();
                logger.fatal(log);
            }
        }
        return this.publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        if (this.privateKey == null) {
            try {
                this.privateKey = (RSAPrivateKey) PemUtil.readPrivateKeyFromFile(this.propertiesDataSource.getRSA_PRIVATE_PATH(), "RSA");
            } catch (IOException e) {
                final String log = ERR_PRIVATE.formatted(this.propertiesDataSource.getRSA_PRIVATE_PATH(), e.getMessage());
                e.printStackTrace();
                logger.fatal(log);
            }
        }
        return this.privateKey;
    }
}
