package qa.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import qa.exceptions.internal.RedisInitializationException;
import qa.source.PasswordPropertyDataSource;
import redis.clients.jedis.DefaultJedisSocketFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisSocketFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class RedisConfiguration {

    private final JedisClientConfig jedisClientConfig;
    private final JedisSocketFactory jedisSocketFactory;

    private static final Logger logger = LogManager.getLogger(RedisConfiguration.class);
    private static final String ERR_LOAD_PASSWORD = "cannot load redis password. path: %s";

    @Autowired
    public RedisConfiguration(PasswordPropertyDataSource propertyDataSource) {
        final String password = getPassword(propertyDataSource);
        final String host = "localhost";
        final int port = 6379;

        this.jedisSocketFactory = new DefaultJedisSocketFactory(new HostAndPort(host, port));
        this.jedisClientConfig = new JedisClientConfig() {
            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public int getDatabase() {
                return 1;
            }
        };
    }

    private String getPassword(PasswordPropertyDataSource propertyDataSource) {
        final StringBuilder sb;
        final String path = propertyDataSource.getREDIS_PASSWORD_PATH();
        try {
            sb = new StringBuilder(new String(
                    Files.readAllBytes(
                            Paths.get(path)
                    ))
            );
        } catch (IOException e) {
            final String err = ERR_LOAD_PASSWORD.formatted(path);
            e.printStackTrace();
            logger.fatal(err);
            throw new RedisInitializationException(err);
        }

        if (sb.charAt(sb.length() - 1) == '\n')
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public JedisClientConfig getJedisClientConfig() {
        return jedisClientConfig;
    }

    public JedisSocketFactory getJedisSocketFactory() {
        return jedisSocketFactory;
    }
}