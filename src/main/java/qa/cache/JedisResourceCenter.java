package qa.cache;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.config.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JedisResourceCenter {

    private final JedisPool jedisPool;

    @Autowired
    public JedisResourceCenter(RedisConfiguration redisConfiguration) {
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setNumTestsPerEvictionRun(-1);

        this.jedisPool = new JedisPool(poolConfig, redisConfiguration.getJedisSocketFactory(), redisConfiguration.getJedisClientConfig());
    }

    public JedisResource getResource() {
        return new JedisResource(jedisPool);
    }
}
