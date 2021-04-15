package qa.util.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import qa.source.PasswordPropertyDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class RedisConfiguration {

    private final String password;

    private static final Logger logger = LogManager.getLogger(RedisConfiguration.class);

    @Autowired
    public RedisConfiguration(PasswordPropertyDataSource propertyDataSource) {
        this.password = getPassword(propertyDataSource);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(1);
        configuration.setPassword(password);
        configuration.setHostName("localhost");
        configuration.setPort(6379);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    private String getPassword(PasswordPropertyDataSource propertyDataSource) {
        StringBuilder sb;

        try {
            sb = new StringBuilder(new String(Files.readAllBytes(Paths.get(propertyDataSource.getREDIS_PASSWORD_PATH()))));
        } catch (IOException e) {
            logger.fatal("cannot load redis password.");
            e.printStackTrace();
            throw new RedisConnectionFailureException("cannot load redis password.");
        }

        if (sb.charAt(sb.length() - 1) == '\n')
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
