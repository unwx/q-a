package qa.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisResource implements AutoCloseable {

    private final JedisPool jedisPool;
    private final Jedis jedis;

    public JedisResource(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.jedis = jedisPool.getResource();
    }

    @Override
    public void close() {
        jedisPool.returnResource(jedis);
    }

    public Jedis getJedis() {
        return jedis;
    }

    @SuppressWarnings("unused")
    private JedisPool getJedisPool() { // getter no allowed.
        return null;
    }
}
