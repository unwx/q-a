package qa.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.config.RedisConfiguration;
import redis.clients.jedis.Jedis;

@Component
public class JedisFactory {

    private final Jedis jedis;

    @Autowired
    public JedisFactory(RedisConfiguration redisConfiguration) {
        this.jedis = new Jedis(redisConfiguration.getJedisSocketFactory(), redisConfiguration.getJedisClientConfig());
    }

    public Jedis getJedis() {
        return jedis;
    }
}
