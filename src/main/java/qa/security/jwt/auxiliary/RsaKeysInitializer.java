package qa.security.jwt.auxiliary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.source.PropertiesDataSource;
import qa.util.PemUtil;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class RsaKeysInitializer {

    private RSAPublicKey publicKey = null;
    private RSAPrivateKey privateKey = null;
    private final PropertiesDataSource propertiesDataSource;

    private final Logger logger = LogManager.getLogger(RsaKeysInitializer.class);

    @Autowired
    public RsaKeysInitializer(PropertiesDataSource propertiesDataSource) {
        this.propertiesDataSource = propertiesDataSource;
    }

    public RSAPublicKey getPublicKey() {
        if (publicKey == null) {
            try {
                publicKey = (RSAPublicKey) PemUtil.readPublicKeyFromFile(
                        propertiesDataSource.getRSA_PUBLIC_PATH(),
                        propertiesDataSource.getJWT_ALGORITHM());
            } catch (IOException e) {
                e.printStackTrace();
                String log =
                        """
                        cannot get the PUBLIC RSA key from the file.\s\
                        filepath: %s
                        Cause: %s\
                        """.formatted(propertiesDataSource.getRSA_PUBLIC_PATH(), e.getMessage());
                logger.fatal(log);
            }
        }
        return publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        if (privateKey == null) {
            try {
                privateKey = (RSAPrivateKey) PemUtil.readPrivateKeyFromFile(
                        propertiesDataSource.getRSA_PRIVATE_PATH(),
                        propertiesDataSource.getJWT_ALGORITHM());
            } catch (IOException e) {
                e.printStackTrace();
                String log =
                        """
                        cannot get the PRIVATE RSA key from the file.\s\
                        filepath: %s
                        Cause: %s\
                        """.formatted(propertiesDataSource.getRSA_PRIVATE_PATH(), e.getMessage());
                logger.fatal(log);
            }
        }
        return privateKey;
    }
}
