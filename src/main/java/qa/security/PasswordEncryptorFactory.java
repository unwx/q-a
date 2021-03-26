package qa.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.source.PasswordPropertyDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class PasswordEncryptorFactory {

    private final String password;

    private static final Logger logger = LogManager.getLogger(PasswordEncryptorFactory.class);
    private static PooledPBEStringEncryptor pooledPBEStringEncryptor;

    @Autowired
    public PasswordEncryptorFactory(PasswordPropertyDataSource propertyDataSource) {
        this.password = readPasswordFromFile(propertyDataSource);
    }

    public PooledPBEStringEncryptor create() {
        if (pooledPBEStringEncryptor == null) {
            pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(password);
            config.setAlgorithm("PBEWithMD5AndDES");
            config.setKeyObtentionIterations("1000");
            config.setPoolSize("1");
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setStringOutputType("base64");
            pooledPBEStringEncryptor.setConfig(config);
        }
        return pooledPBEStringEncryptor;
    }

    private String readPasswordFromFile(PasswordPropertyDataSource propertyDataSource) {
        try {
            StringBuilder sb = new StringBuilder(new String(Files.readAllBytes(Paths.get(propertyDataSource.getPASSWORD_PATH()))));
            if (sb.charAt(sb.length() - 1) == '\n')
                sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (IOException e) {
            logger.fatal("cannot get password for passwordEncoder. path: " + propertyDataSource.getPASSWORD_PATH());
            e.printStackTrace();
            return null;
        }
    }
}
