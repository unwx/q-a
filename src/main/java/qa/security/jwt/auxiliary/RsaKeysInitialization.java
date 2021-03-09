package qa.security.jwt.auxiliary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.source.PropertiesDataSource;
import qa.util.PemUtil;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class RsaKeysInitialization {

    private RSAPublicKey publicKey = null;
    private RSAPrivateKey privateKey = null;
    private final PropertiesDataSource propertiesDataSource;

    @Autowired
    public RsaKeysInitialization(PropertiesDataSource propertiesDataSource) {
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
                System.out.println("cannot get the --public-- rsa key from the file. Cause: " + e.getCause());
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
                System.out.println("cannot get the --private-- rsa key from the file. Cause: " + e.getCause());
            }
        }
        return privateKey;
    }
}
