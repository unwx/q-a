package qa.util.mock;

import org.mockito.Mockito;
import qa.cache.JedisResourceCenter;
import qa.config.RedisConfiguration;
import qa.source.PasswordPropertyDataSource;

public class JedisMockTestUtil {

    private static RedisConfiguration redisConfiguration;
    private static JedisResourceCenter jedisResourceCenter;

    private JedisMockTestUtil() {
    }

    public static JedisResourceCenter mockJedisFactory() {
        if (redisConfiguration == null)
            redisConfiguration = Mockito.spy(new RedisConfiguration(mockPPDataSource()));
        if (jedisResourceCenter == null)
            jedisResourceCenter = new JedisResourceCenter(redisConfiguration);
        return jedisResourceCenter;
    }

    private static PasswordPropertyDataSource mockPPDataSource() {
        PasswordPropertyDataSource propertyDataSource = Mockito.mock(PasswordPropertyDataSource.class);
        Mockito.lenient().when(propertyDataSource.getREDIS_PASSWORD_PATH()).thenReturn("/disk/main/forProjects/qa/redis/password.pass");
        return propertyDataSource;
    }
}
