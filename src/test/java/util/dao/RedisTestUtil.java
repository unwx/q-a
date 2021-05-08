package util.dao;

import qa.cache.JedisResource;
import qa.cache.JedisResourceCenter;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisTestUtil {

    private final JedisResourceCenter jedisResourceCenter;

    public RedisTestUtil(JedisResourceCenter jedisResourceCenter) {
        this.jedisResourceCenter = jedisResourceCenter;
    }

    public Set<String> getAllKeys() {
        final Set<String> keys;
        try (JedisResource jedisResource = jedisResourceCenter.getResource()) {
            final Jedis jedis = jedisResource.getJedis();
            keys = jedis.keys("*");
        }
        return keys;
    }
}
